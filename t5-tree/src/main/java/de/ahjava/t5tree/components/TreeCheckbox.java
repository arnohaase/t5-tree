package de.ahjava.t5tree.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.AfterRenderTemplate;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import de.ahjava.t5tree.tree.TreeHierarchyTracker;


@Import(library="style/t5tree.js")
public class TreeCheckbox {
    @InjectComponent private ClientElement check;
    @Environmental private JavaScriptSupport jsSupport;
    
    @Parameter(required=true, defaultPrefix=BindingConstants.PROP)
    private boolean checked;

    @Parameter(required=false, defaultPrefix=BindingConstants.LITERAL)
    private String checkboxClass;

    @Parameter(required=false, defaultPrefix=BindingConstants.LITERAL)
    private String checkboxOnClick; //TODO use a more general solution
    
    public boolean isChecked() {
        return checked;
    }
    
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getCheckboxClass() {
        if (checkboxClass == null) {
            return "";
        }
        return checkboxClass;
    }
    
    public String getCheckboxOnClick() {
        if (checkboxOnClick == null) {
            return "";
        }
        return checkboxOnClick;
    }
    
    @AfterRenderTemplate
    public void afterRenderBody() {
        TreeHierarchyTracker.push(check.getClientId());
        jsSupport.addScript("t5tree.refreshCheckboxFromChildValues($j('#%s').get(0));", check.getClientId());
    }
    
    public void cleanupAfterNode() {
        TreeHierarchyTracker.pop(); // TODO verify if this is always called
    }
    
    public String getHierarchyClasses() {
        return TreeHierarchyTracker.getCheckboxHierarchyCssClasses();
    }
}
