package de.ahjava.t5tree.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;


//TODO konfigurierbar, ob nur +/- oder die ganze Titelzeile auf- und zuklappen
//TODO Animation optimieren: zun�chst +/- Icon auf "transition"-Icon setzen, dann Animation, dann auf anderes Icon setzen
//TODO beautiful default styling
//TODO build an actual tree component on top of this
//TODO JS Contribution, die Elemente "children-of-{id}" und "descendents-of-{id}"-Klassen fuer Kinder / Enkel von TreeNodes erzeugt - oder ThreadLocal beim Rendering im Server, der mithaelt?
//TODO class or optional block as default for before-row-content
@Import(library="style/t5tree.js", stylesheet="style/t5tree.css")
public class TreeNode {
    /**
     * one of the animation parameters for jQuery - e.g. 'slow' and 'fast'
     */
    @Property
    @Parameter(required=false, defaultPrefix=BindingConstants.LITERAL)
    private String animation;
    
    @Parameter(required=false, value="literal:", defaultPrefix=BindingConstants.LITERAL)
    private String nodeClass;

    @Parameter(required=false, value="literal:", defaultPrefix=BindingConstants.LITERAL)
    private String rowClass;

    @Property
    @Parameter(required=false, value="false", defaultPrefix=BindingConstants.PROP)
    private boolean isOpen;

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
    
    @Parameter(required=false, value="tree-before-text", defaultPrefix=BindingConstants.LITERAL)
    private String treeBeforeContentTextClass;
    
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

    @Environmental private JavaScriptSupport jsSupport;

    
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

    public String getTreeBeforeContentTextClass() {
        return treeBeforeContentTextClass;
    }

    public String getTreeChildrenClass() {
        return treeChildrenClass;
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
    
    @AfterRender
    public void afterRender() {
        jsSupport.addScript("$j('#%s').val('%s');", treeOpenClose.getClientId(), "" + isOpen);
    }
}

