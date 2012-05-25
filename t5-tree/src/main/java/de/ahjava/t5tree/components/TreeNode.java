package de.ahjava.t5tree.components;

import javax.inject.Inject;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.runtime.RenderCommand;
import org.apache.tapestry5.runtime.RenderQueue;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;


@Import(library="style/t5tree.js", stylesheet="style/t5tree.css")
public class TreeNode {
    /**
     * one of the animation parameters for jQuery - e.g. 'slow' and 'fast'
     */
    @Parameter(required=false, defaultPrefix=BindingConstants.LITERAL)
    private String animation;
    
    @Parameter(required=false, value="literal:", defaultPrefix=BindingConstants.LITERAL)
    private String nodeClass;

    @Parameter(required=false, value="literal:", defaultPrefix=BindingConstants.LITERAL)
    private String rowClass;

    @Property
    @Parameter(required=false, value="false", defaultPrefix=BindingConstants.PROP)
    private boolean isOpen;

    @SuppressWarnings("unused")
    @Property
    @Parameter(required=false, value="literal:", defaultPrefix=BindingConstants.LITERAL)
    private String iconOpenClosedCommonClass;
    
    @Property
    @Parameter(required=false, value="tree-icon-open", defaultPrefix=BindingConstants.LITERAL)
    private String iconOpenClass;

    @Property
    @Parameter(required=false, value="tree-icon-closed", defaultPrefix=BindingConstants.LITERAL)
    private String iconClosedClass;

    @Property
    @Parameter(required=false, value="true", defaultPrefix=BindingConstants.LITERAL)
    private boolean rememberOpenClose;
    
    @Property @Parameter private Boolean checked;
    @Property @Parameter private RenderCommand checkboxCompartment;
    
    @Property @Parameter(defaultPrefix=BindingConstants.LITERAL) private String iconClass;
    @Property @Parameter(defaultPrefix=BindingConstants.ASSET)   private Asset iconUrl;
    
    @Property
    @Parameter(required=false, value="tree-children", defaultPrefix=BindingConstants.LITERAL)
    private String treeChildrenClass;
    
    /**
     * either this or the component must be provided
     */
    @Parameter(required=false, defaultPrefix=BindingConstants.MESSAGE)
    private String rowContentText;
    
    @Parameter(required=false, defaultPrefix=BindingConstants.BLOCK)
    private Object rowContentComponent;

    @InjectComponent private ClientElement hiddenIsOpenField;
    @InjectComponent private ClientElement treeOpenClose;
    @InjectComponent private TreeCheckbox defaultCheckbox;
    @Inject          private Block simpleCheckboxComponent;
    
    @Environmental private JavaScriptSupport jsSupport;

    
    public String getAnimation() {
        return animation != null ? "'" + animation + "'" : "";
    }
    
    public String getNodeClass() {
        return nodeClass;
    }
    
    public String getRowClass() {
        return rowClass;
    }
    
    public boolean getHasContentComponent() {
        return rowContentComponent != null;
    }
    
    public Object getRowContentComponent() {
        return rowContentComponent;
    }
    
    public String getRowContentText() {
        return rowContentText;
    }

    public String getTreeOpenClosedClass() {
        return isOpen ? "tree-node-open" : "tree-node-closed";
    }
    
    public String getOpenClosedStyle() {
        return isOpen ? "block" : "none"; // to avoid flicker during rendering - if we first display the children and later hide them, that causes a flicker 
    }

    public String getOpenCloseSelector() {
        if (! rememberOpenClose) {
            return "this";
        }
        return "'#" + hiddenIsOpenField.getClientId() + "'";
    }
    
    public String getIconOpenClosedClass() {
        return isOpen ? iconOpenClass : iconClosedClass;
    }

    public ClientElement getTreeOpenCloseComponent() {
        return treeOpenClose;
    }
    
    private boolean hasCheckbox() {
        return checked != null;
    }
    
    public RenderCommand getTheCheckboxCompartment() {
        if (!hasCheckbox()) {
            return RENDER_NOTHING;
        }
        if(checkboxCompartment != null) {
            return checkboxCompartment;
        }
        else {
            return (RenderCommand) simpleCheckboxComponent; 
        }
    }

    public boolean getHasIconClass() {
        return iconClass != null;
    }
    
    public boolean getHasIconUrl() {
        return iconUrl != null;
    }
    
    @AfterRender
    public void afterRender() {
        if(hasCheckbox()) {
            defaultCheckbox.cleanupAfterNode();
        }
        jsSupport.addScript("$j('#%s').val('%s');", treeOpenClose.getClientId(), "" + isOpen);
    }
    
    private static final RenderCommand RENDER_NOTHING = new RenderCommand() {
        @Override
        public void render(MarkupWriter writer, RenderQueue queue) {
        }
    };
}


