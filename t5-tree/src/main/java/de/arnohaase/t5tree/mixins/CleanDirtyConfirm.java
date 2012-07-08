package de.arnohaase.t5tree.mixins;

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;


@Import(library="CleanDirty.js")
public class CleanDirtyConfirm {
    @Inject private JavaScriptSupport jss;
    @InjectContainer private ClientElement element;
    @Environmental private FormSupport formSupport;
    
    @Parameter(value="true") private boolean confirmEnabled;
    @Parameter(required=true, defaultPrefix=BindingConstants.LITERAL) private String dirtyClass;
    @Parameter(required=true) private String confirmQuestion; //TODO i18n, default
    
    /**
     * optional root element in the DOM below which all elements are reset to their clean state; defaults to
     *  the surrounding form.
     */
    @Parameter private ClientElement rootElement;
    
    @AfterRender
    public void afterRender() {
        if (! confirmEnabled) {
            return;
        }
        
        final String rootElementId = rootElement != null ? rootElement.getClientId() : formSupport.getClientId();
        
        final StringBuilder js = new StringBuilder();
        js.append("$j('#%s').click(function(event) {");
        js.append("  if ($j('#%s').cleanDirty('isDirty', '.%s')) {");
        js.append("    if (! confirm('%s')) {");
        js.append("      event.preventDefault();");
        js.append("    }");
        js.append("  }");
        js.append("});");
        
        jss.addScript(js.toString(), element.getClientId(), rootElementId, dirtyClass, confirmQuestion);
    }
}
