package de.ahjava.t5tree.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.runtime.RenderCommand;
import org.apache.tapestry5.runtime.RenderQueue;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import de.ahjava.t5tree.tree.TreeModel;


@Import(library="style/t5tree.js", stylesheet="style/t5tree.css")
public class Tree<T> {
    @Property
    @Parameter (required=true)
    private TreeModel<T> model;

    @Parameter(defaultPrefix=BindingConstants.LITERAL)
    private String animation;
    
    // TODO
//    @Property
//    @Parameter (value="false", defaultPrefix=BindingConstants.LITERAL)
//    private boolean rememberOpenClosed;
    
    // TODO
//    @Property
//    private String mangledOpenNodes;
    
    @Property
    @Parameter (value="block:defaultNodeLabel")
    private RenderCommand nodeLabelRenderer;

    @Parameter
    private T currentNode;
    private boolean isCurrentNodeLast;
    
    @InjectComponent private Zone lazyLoadZone;
    
    @Inject private Request request;
    
    @Inject private AjaxResponseRenderer ajaxResponseRenderer;
    @Inject private ComponentResources resources;
    @Environmental private JavaScriptSupport jss;

    
    private Map<String, String> lazyZoneIds = new HashMap<String, String>();
    
    private static final RenderCommand RENDER_CLOSE_TAG = new RenderCommand() {
        public void render(MarkupWriter writer, RenderQueue queue) {
            writer.end();
        }
    };

    private RenderCommand cmdToRenderOpen(final String name, final String... attributes) {
        return new RenderCommand() {
            @Override
            public void render(MarkupWriter writer, RenderQueue queue) {
                writer.element(name, attributes);
            }
        };
    }
    
    private RenderCommand cmdToRenderEmptyElement(final String name, final String... attributes) {
        return new RenderCommand() {
            @Override
            public void render(MarkupWriter writer, RenderQueue queue) {
                writer.element(name, attributes);
                writer.end();
            }
        };
    }
    
    private RenderCommand cmdToRenderOpenCloseControl(final T node, final boolean forEagerLoad, final boolean isLast, final boolean forceOpen) {
        return new RenderCommand() {
            @Override
            public void render(MarkupWriter writer, RenderQueue queue) {
                final String openCloseId = jss.allocateClientId(resources); 
                final Element span = writer.element("span");
                span.attribute("id", openCloseId);
                span.addClassName("tree-open-close");
                span.attribute("value", String.valueOf(model.isExpanded(node)));
                
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
                    final Link lazyLoadLink = resources.createEventLink("lazyLoadTreeChildren", getLazyZoneId(), model.getId(node), isLast);
                    span.attribute("onclick",
                            "console.log('" + lazyLoadZone.getClientId() + "');" +
                        "var zoneObject=Tapestry.findZoneManagerForZone('" + lazyLoadZone.getClientId() + "');" + 
                        "zoneObject.updateFromURL('" + lazyLoadLink + "', {});"
                    );
                }
                queue.push(RENDER_CLOSE_TAG);
                queue.push(cmdToRenderEmptyElement("i", "class", getIconOpenClosedCommonClass(node) + " " + getIconOpenClosedClass(node, forceOpen)));
                
                jss.addScript("$j('#%s').val('%s');", openCloseId, "" + isOpen(node, forceOpen));
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
                    queue.push(nodeLabelRenderer); //TODO enhance this - in sync with the (to be done) TreeLeaf component
                    queue.push(cmdToRenderOpen("div", "class", "tree-leaf " + getLeafClass(node)));
                }
                else {
                    final boolean requiresLazyLoadZone = ! model.isEagerlyTransferred(node) && ! model.isExpanded(node) && !isInsideLazyLoadZone && !isLazyLoadUpdate;
                    
                    if (requiresLazyLoadZone) {
                        queue.push((RenderCommand) lazyLoadZone);
                    }
                    else {
                        final boolean areChildrenTransferred = model.isEagerlyTransferred(node) || model.isExpanded(node) || isLazyLoadUpdate; 

                        writer.element("div", "class", "tree-folder " + getTreeOpenClosedClass(node, isLazyLoadUpdate) + " " + emptyForNull(model.getNodeClass(node)));
                        queue.push(RENDER_CLOSE_TAG);

                        if (areChildrenTransferred) {
                            queue.push(cmdToRenderChildren(node, isLazyLoadUpdate));
                        }

                        queue.push(RENDER_CLOSE_TAG);
                        queue.push(nodeLabelRenderer);
                        queue.push(cmdToRenderOpenCloseControl(node, areChildrenTransferred, isLast, isLazyLoadUpdate));
                        queue.push(cmdToRenderOpen("div", "class", "tree-row " + getRowClass(node)));
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
    
    public void onLazyLoadTreeChildren(String zoneId, String parentNodeId, boolean isLast) {
        ajaxResponseRenderer.addRender(zoneId, cmdToRenderNode(model.fromId(parentNodeId), isLast, false, true));
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
        return forceOpen || model.isExpanded(node);
    }

    private String getOpenClosedStyle(T node) {
        return isOpen(node, false) ? "block" : "none";
    }
    
    private String getTreeOpenClosedClass(T node, boolean forceOpen) {
        return isOpen(node, forceOpen) ? "tree-node-open" : "tree-node-closed";
    }
    
    private String getIconOpenClosedCommonClass(T node) {
        return ""; //TODO default / tree parameter, delegate to TreeModel
    }

    private String getIconOpenClass(T node) {
        //TODO default / tree parameter, delegate to TreeModel
        return "tree-icon-open";
    }
    
    private String getIconClosedClass(T node) {
        //TODO default / tree parameter, delegate to TreeModel
        return "tree-icon-closed";
    }
    
    private String getIconOpenClosedClass(T node, boolean forceOpen) {
        return isOpen(node, forceOpen) ? getIconOpenClass(node) : getIconClosedClass(node);
    }
    
    private String getChildrenDivClass(T node) { 
        //TODO default / tree parameter, delegate to TreeModel
        return "tree-children";
    }
    
    private String getRowClass(T node) {
        //TODO 
        return "";
    }
    
    private String getLeafClass(T node) {
        //TODO
        return "";
    }
}


