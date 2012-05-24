package de.ahjava.t5tree.components;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentAction;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.runtime.RenderCommand;
import org.apache.tapestry5.runtime.RenderQueue;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import de.ahjava.t5tree.tree.TreeCheckModel;
import de.ahjava.t5tree.tree.TreeExpansionModel;
import de.ahjava.t5tree.tree.TreeHierarchyTracker;
import de.ahjava.t5tree.tree.TreeModel;


@Import(library="style/t5tree.js", stylesheet="style/t5tree.css")
public class Tree<T> {
    @Parameter (required=true) private TreeModel<T> model;
    
    /**
     * The tree component recreates the expansion model with every submit, so clients need not store it persistently
     */
    @Parameter private TreeExpansionModel expansionModel;
    
    /**
     * The tree component recreates the check model with every submit, so clients need not store it persistently
     */
    @Parameter private TreeCheckModel checkModel;
    
    @Parameter(defaultPrefix=BindingConstants.LITERAL)
    private String animation;
    
    @Parameter (value="true", defaultPrefix=BindingConstants.LITERAL)
    private boolean rememberOpenClosed;
    
    @Parameter (value="false", defaultPrefix=BindingConstants.LITERAL)
    private boolean showCheckedState;
    
    @Parameter (value="true", defaultPrefix=BindingConstants.LITERAL)
    private boolean rememberCheckedState;
    
    @Parameter(                                  defaultPrefix=BindingConstants.LITERAL) private String iconOpenClosedCommonClass;
    @Parameter(value="literal:tree-icon-open",   defaultPrefix=BindingConstants.LITERAL) private String iconOpenClass;
    @Parameter(value="literal:tree-icon-closed", defaultPrefix=BindingConstants.LITERAL) private String iconClosedClass;
    
    @Parameter(value="literal:tree-children", defaultPrefix=BindingConstants.LITERAL) private String childrenDivClass;
    @Parameter(                               defaultPrefix=BindingConstants.LITERAL) private String nodeRowClass;
    @Parameter(                               defaultPrefix=BindingConstants.LITERAL) private String leafClass;
    
    @Property
    @Parameter (value="block:defaultNodeLabel")
    private RenderCommand nodeLabelRenderer;

    @Parameter private T currentNode;
    private boolean isCurrentNodeLast;
    
    @InjectComponent private Zone lazyLoadZone;
    
    @Inject private AjaxResponseRenderer ajaxResponseRenderer;
    @Inject private ComponentResources resources;
    @Inject private Request request;

    @Environmental         private JavaScriptSupport jss;
    @Environmental(false) private FormSupport formSupport;
    
    private Map<String, String> lazyZoneIds = new HashMap<String, String>();
    
    private static final RenderCommand RENDER_CLOSE_TAG = new RenderCommand() {
        public void render(MarkupWriter writer, RenderQueue queue) {
            writer.end();
        }
    };

    private static class ProcessOpenCloseOnSubmit<T> implements ComponentAction<Tree<T>> {
        private static final long serialVersionUID = 1L;
        
        private final String nodeId;
        private final String hiddenFieldId;
        
        public ProcessOpenCloseOnSubmit(String nodeId, String hiddenFieldId) {
            this.nodeId = nodeId;
            this.hiddenFieldId = hiddenFieldId;
        }

        @Override
        public void execute(Tree<T> component) {
            component.restoreOpenCloseState(nodeId, hiddenFieldId);
        }
    }
    
    private static class ProcessCheckOnSubmit<T> implements ComponentAction<Tree<T>> {
        private static final long serialVersionUID = 1L;
    
        private final String nodeId;
        private final String checkboxId;
        
        public ProcessCheckOnSubmit(String nodeId, String checkboxId) {
            this.nodeId = nodeId;
            this.checkboxId = checkboxId;
        }

        @Override
        public void execute(Tree<T> component) {
            component.restoreCheckedState(nodeId, checkboxId);
        }
    }
    
    private final RenderCommand POP_FROM_CHECKBOX_HIERARCHY = new RenderCommand() {
        @Override
        public void render(MarkupWriter writer, RenderQueue queue) {
            if (hasCheckboxes()) {
                TreeHierarchyTracker.pop(); //TODO register with Tapestry's global pre-request cleanup service
            }
        }
    };
    
    private final RenderCommand CLEAR_CHECKBOX_HIERARCHY = new RenderCommand() {
        @Override
        public void render(MarkupWriter writer, RenderQueue queue) {
            if (hasCheckboxes()) {
                TreeHierarchyTracker.clear();
            }
        }
    };
    
    private RenderCommand cmdToRenderOpen(final String name, final String... attributes) {
        return new RenderCommand() {
            @Override
            @SuppressWarnings("all")
            public void render(MarkupWriter writer, RenderQueue queue) {
                writer.element(name, attributes);
            }
        };
    }
    
    private RenderCommand cmdToRenderEmptyElement(final String name, final String... attributes) {
        return new RenderCommand() {
            @Override
            @SuppressWarnings("all")
            public void render(MarkupWriter writer, RenderQueue queue) {
                writer.element(name, attributes);
                writer.end();
            }
        };
    }
    
    @BeginRender
    public void beginRender() {
        if (rememberOpenClosed && formSupport != null) {
            if (expansionModel == null) {
                throw new RuntimeException ("An expansion model must be provided for a tree if its state is to be remembered. If that is not desired, set rememberOpenClosed to false.");
            }
        }
    }
    
    private boolean isExpanded(T node) {
        if (expansionModel == null) {
            return model.isInitiallyExpanded(node);
        }
        return expansionModel.isExpanded(model.getId(node));
    }

    private boolean hasCheckboxes() {
        return showCheckedState || checkModel != null;
    }
    
    private RenderCommand cmdToRenderCheckbox(final T node) {
        return new RenderCommand() {
            @Override
            public void render(MarkupWriter writer, RenderQueue queue) {
                if (! hasCheckboxes()) {
                    return;
                }
                
                final String checkboxClientId = jss.allocateClientId(resources);

                if (checkModel != null && formSupport != null) {
                    formSupport.store(Tree.this, new ProcessCheckOnSubmit(model.getId(node), checkboxClientId));
                }

                if(! model.isLeaf(node)) {
                    jss.addScript("t5tree.refreshCheckboxFromChildValues($j('#%s').get(0));", checkboxClientId);
                }
                
                writer.element("span", "class", "tree-checkbox "); //TODO + getCheckboxClass(node));
                final Element checkElement = writer.element("input", 
                        "type", "checkbox",
                        "id", checkboxClientId,
                        "name", checkboxClientId,
                        "class", TreeHierarchyTracker.getCheckboxHierarchyCssClasses(),
                        "onclick", "t5tree.handleCheckBoxChange(this);"); //TODO + getCheckboxOnClick --> or provide hooks for subclassing the tree component?
                final boolean checked = isChecked(node);
                if (checked) {
                    checkElement.attribute("checked", "checked");
                }
                
                writer.end(); // input
                writer.end(); // span
                
                TreeHierarchyTracker.push(checkboxClientId);
            }
        };
    }
    
    private boolean isChecked(T node) {
        if (checkModel != null) {
            return checkModel.isChecked(model.getId(node));
        }
        else {
            return model.isInitiallyChecked(node);
        }
    }
    
    private RenderCommand cmdToRenderOpenCloseControl(final T node, final boolean forEagerLoad, final boolean isLast, final boolean forceOpen) {
        return new RenderCommand() {
            @Override
            public void render(MarkupWriter writer, RenderQueue queue) {
                final String openCloseId = jss.allocateClientId(resources);
                
                writer.element("input", "type", "hidden", "id", openCloseId, "name", openCloseId, "value", "" + isOpen(node, forceOpen));
                writer.end();
                
                if(rememberOpenClosed && formSupport != null) {
                    formSupport.store(Tree.this, new ProcessOpenCloseOnSubmit<T>(model.getId(node), openCloseId));
                }
                
                final Element span = writer.element("span");
//                span.attribute("id", openCloseId);
                span.addClassName("tree-open-close");
                span.attribute("value", String.valueOf(isExpanded(node)));
                
                if (forEagerLoad) {
                    span.attribute("onclick", 
                        "var isOpen = ($j('#" + openCloseId + "').val() === 'false');" + 
                        "$j('#" + openCloseId + "').val('' + isOpen);" + 
                        "$j(this).parent().siblings().toggle(" + getAnimation() + ");" + 
                        "$j(this).children().removeClass('" + getIconOpenClass(node) + "');" +
                        "$j(this).children().removeClass('" + getIconClosedClass(node) + "');" +
                        "if(isOpen)" +
                        "  $j(this).children().addClass('" + getIconOpenClass(node) + "');" +
                        "else " +
                        "  $j(this).children().addClass('" + getIconClosedClass(node) + "');"
                    );
                }
                else {
                    boolean first = true;
                    final StringBuilder checkboxIdHierarchy = new StringBuilder();
                    for (String checkboxId: TreeHierarchyTracker.getAncesterCheckboxIds()) {
                        if (first) {
                            first = false;
                        }
                        else {
                            checkboxIdHierarchy.append(" ");
                        }
                        checkboxIdHierarchy.append(checkboxId);
                    }
                    
                    final Link lazyLoadLink = resources.createEventLink("lazyLoadTreeChildren", getLazyZoneId(), model.getId(node), isLast, checkboxIdHierarchy);
                    span.attribute("onclick",
                            "console.log('" + lazyLoadZone.getClientId() + "');" +
                        "var zoneObject=Tapestry.findZoneManagerForZone('" + lazyLoadZone.getClientId() + "');" + 
                        "zoneObject.updateFromURL('" + lazyLoadLink + "', {});"
                    );
                }
                queue.push(RENDER_CLOSE_TAG);
                queue.push(cmdToRenderEmptyElement("i", "class", getIconOpenClosedCommonClass(node) + " " + getIconOpenClosedClass(node, forceOpen)));
            }
        };
    }
    
    private RenderCommand cmdToRenderNode(final T node, final boolean isLast, final boolean isInsideLazyLoadZone, final boolean isLazyLoadUpdate) { 
        return new RenderCommand() {
            @Override
            public void render(MarkupWriter writer, RenderQueue queue) {
                currentNode = node;
                isCurrentNodeLast = isLast;
                
                if (model.isLeaf(node)) {
                    queue.push(RENDER_CLOSE_TAG);
                    queue.push(POP_FROM_CHECKBOX_HIERARCHY);
                    queue.push(nodeLabelRenderer); //TODO enhance this - in sync with the (to be done) TreeLeaf component
                    queue.push(cmdToRenderCheckbox(node));
                    queue.push(cmdToRenderOpen("div", "class", "tree-leaf " + getLeafClass(node)));
                }
                else {
                    final boolean requiresLazyLoadZone = ! model.isEagerlyTransferred(node) && ! isExpanded(node) && !isInsideLazyLoadZone && !isLazyLoadUpdate;
                    
                    if (requiresLazyLoadZone) {
                        queue.push((RenderCommand) lazyLoadZone);
                    }
                    else {
                        final boolean areChildrenTransferred = model.isEagerlyTransferred(node) || isExpanded(node) || isLazyLoadUpdate; 

                        writer.element("div", "class", "tree-folder " + getTreeOpenClosedClass(node, isLazyLoadUpdate) + " " + emptyForNull(model.getNodeClass(node)));
                        queue.push(RENDER_CLOSE_TAG);
                        queue.push(POP_FROM_CHECKBOX_HIERARCHY);

                        if (areChildrenTransferred) {
                            queue.push(cmdToRenderChildren(node, isLazyLoadUpdate));
                        }

                        queue.push(RENDER_CLOSE_TAG);
                        queue.push(nodeLabelRenderer);
                        queue.push(cmdToRenderCheckbox(node));
                        queue.push(cmdToRenderOpenCloseControl(node, areChildrenTransferred, isLast, isLazyLoadUpdate));
                        queue.push(cmdToRenderOpen("div", "class", "tree-row " + getNodeRowClass(node)));
                    }
                }
            }
        };
    }
    
    public RenderCommand getRenderCurrentNodeForLazyLoad() {
        return cmdToRenderNode(currentNode, isCurrentNodeLast, true, false);
    }
    
    private RenderCommand cmdToRenderChildren(final T node, final boolean isLazyLoadUpdate) {
        return new RenderCommand() {
            @Override
            public void render(MarkupWriter writer, RenderQueue queue) {
                queue.push(RENDER_CLOSE_TAG);
                queue.push(cmdToRenderNodes(model.getChildren(node)));

                if (isLazyLoadUpdate) {
                    final String clientId = jss.allocateClientId(resources);
                    queue.push(cmdToRenderOpen("div", "id", clientId, "style", "display: " + getOpenClosedStyle(node), "class", getChildrenDivClass(node)));
                    jss.addScript("$j('#%s').show(%s)", clientId, getAnimation());
                }
                else {
                    queue.push(cmdToRenderOpen("div", "style", "display: " + getOpenClosedStyle(node), "class", getChildrenDivClass(node)));
                }
            }
        };
    }
    
    private RenderCommand cmdToRenderNodes(final List<T> nodes) {
        return new RenderCommand() {
            @Override
            public void render(MarkupWriter writer, RenderQueue queue) {
                for (int i=nodes.size()-1; i>=0; i--) {
                    queue.push(cmdToRenderNode(nodes.get(i), i == nodes.size()-1, false, false));
                }
            }
        };
    }
    
    private RenderCommand cmdToRenderLazyLoad (final T node, final boolean isLast, final boolean isInsideLazyLoadZone, final boolean isLazyLoadUpdate, final List<String> checkboxIdHierarchy) {
        return new RenderCommand() {
            @Override
            public void render(MarkupWriter writer, RenderQueue queue) {
                queue.push(CLEAR_CHECKBOX_HIERARCHY);
                queue.push(cmdToRenderNode(node, isLast, isInsideLazyLoadZone, isLazyLoadUpdate));
                queue.push(new RenderCommand() {
                    @Override
                    public void render(MarkupWriter writer, RenderQueue queue) {
                        for (String checkboxId: checkboxIdHierarchy) {
                            TreeHierarchyTracker.push(checkboxId);
                        }
                    }
                });
            }
        };
    }
    
    public void onLazyLoadTreeChildren(String zoneId, String parentNodeId, boolean isLast, String checkboxHierarchy) {
        ajaxResponseRenderer.addRender(zoneId, cmdToRenderLazyLoad(model.fromId(parentNodeId), isLast, false, true, Arrays.asList(checkboxHierarchy.split(" "))));
    }
    
    public String getLazyZoneId() {
        final String nodeId = model.getId(currentNode);
        
        if (lazyZoneIds.containsKey(nodeId)) {
            return lazyZoneIds.get(nodeId);
        }
        else {
            final String result = jss.allocateClientId(resources);
            lazyZoneIds.put(nodeId, result);
            return result;
        }
    }
    
    public RenderCommand getRenderRootNodes() {
        return cmdToRenderNodes(model.getRootNodes());
    }
    
    public void restoreOpenCloseState(String nodeId, String hiddenFieldId) {
        final String value = request.getParameter(hiddenFieldId);
        expansionModel.setExpanded(nodeId, "true".equals(value));
    }
    
    public void restoreCheckedState(String nodeId, String checkboxId) {
        final String value = request.getParameter(checkboxId);
        System.out.println("restoring " + (value != null) + " from " + checkboxId + " for " + nodeId);
        checkModel.setChecked(nodeId, value != null);
    }

    public String getCurrentNodeLabel() {
        return model.getLabel(currentNode);
    }
    
    private static String emptyForNull(String s) {
        return s != null ? s : "";
    }
    
    private String getAnimation() {
        return animation != null ? "'" + animation + "'" : "";
    }
    
    public boolean isOpen(T node, boolean forceOpen) {
        return forceOpen || isExpanded(node);
    }

    private String getOpenClosedStyle(T node) {
        return isOpen(node, false) ? "block" : "none";
    }
    
    private String getTreeOpenClosedClass(T node, boolean forceOpen) {
        return isOpen(node, forceOpen) ? "tree-node-open" : "tree-node-closed";
    }
    
    private String getIconOpenClosedCommonClass(T node) {
        final String perNode = model.getIconOpenClosedCommonClass(node);
        return emptyForNull(perNode != null ? perNode : iconOpenClosedCommonClass);
    }

    private String getIconOpenClass(T node) {
        final String perNode = model.getIconOpenClass(node);
        return emptyForNull(perNode != null ? perNode : iconOpenClass);
    }
    
    private String getIconClosedClass(T node) {
        final String perNode = model.getIconClosedClass(node);
        return emptyForNull(perNode != null ? perNode : iconClosedClass);
    }
    
    private String getIconOpenClosedClass(T node, boolean forceOpen) {
        return isOpen(node, forceOpen) ? getIconOpenClass(node) : getIconClosedClass(node);
    }
    
    private String getChildrenDivClass(T node) { 
        final String perNode = model.getChildrenDivClass(node);
        return emptyForNull(perNode != null ? perNode : childrenDivClass);
    }
    
    private String getNodeRowClass(T node) {
        final String perNode = model.getNodeRowClass(node);
        return emptyForNull(perNode != null ? perNode : nodeRowClass);
    }
    
    private String getLeafClass(T node) {
        final String perNode = model.getLeafClass(node);
        return emptyForNull(perNode != null ? perNode : leafClass);
    }
}


