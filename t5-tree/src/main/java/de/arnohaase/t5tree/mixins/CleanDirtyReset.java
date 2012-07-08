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
public class CleanDirtyReset {
    @Inject private JavaScriptSupport jss;
    @InjectContainer private ClientElement element;
    @Environmental private FormSupport formSupport;
    
    @Parameter(required=true, defaultPrefix=BindingConstants.LITERAL) private String dirtyClass;
    @Parameter(defaultPrefix=BindingConstants.LITERAL, value="true") private boolean preventDefault;
    
    /**
     * optional root element in the DOM below which all elements are reset to their clean state; defaults to
     *  the surrounding form.
     */
    @Parameter private ClientElement rootElement;
    
    @AfterRender
    public void afterRender() {
        final String rootElementId = rootElement != null ? rootElement.getClientId() : formSupport.getClientId();
        
        final StringBuilder js = new StringBuilder();
        js.append("$j('#%s').click(function(event) {");
        js.append("  $j('#%s').cleanDirty('reset', '.%s');");
        if(preventDefault) {
            js.append("  event.preventDefault();");
        }
        js.append("});");
        
        jss.addScript(js.toString(), element.getClientId(), rootElementId, dirtyClass);
    }
}
