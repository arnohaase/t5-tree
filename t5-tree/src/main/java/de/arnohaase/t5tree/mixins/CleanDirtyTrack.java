package de.arnohaase.t5tree.mixins;

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;


@Import(library="CleanDirty.js")
public class CleanDirtyTrack {
    @Inject private JavaScriptSupport jss;
    @InjectContainer private ClientElement element;
    
    @Parameter(defaultPrefix=BindingConstants.LITERAL) private String dirtyClass;
    @Parameter(defaultPrefix=BindingConstants.LITERAL) private String cleanClass;
    
    @AfterRender
    public void afterRender() {
        if (cleanClass != null) {
            jss.addScript("$j('#%s').cleanDirty('track', '%s', '%s');", element.getClientId(), dirtyClass, cleanClass);
        }
        else  {
            jss.addScript("$j('#%s').cleanDirty('track', '%s');", element.getClientId(), dirtyClass);
        }
//        
//        
//        
//        js.append("(function() {");
//        js.append("  var el=$j('#%s');");
//        js.append("  el.data('cleanValue', el.val());");
//        js.append("  el.change(function() {");
//        if (cleanClass != null) {
//            js.append("    if(el.val() === el.data('cleanValue')) el.addClass('" + cleanClass + "'); else el.removeClass('" + cleanClass + "');");
//        }
//        if (dirtyClass != null) {
//            js.append("    if(el.val() === el.data('cleanValue')) el.removeClass('" + dirtyClass + "'); else el.addClass('" + dirtyClass + "');");
//        }
//        js.append("  });");
//        js.append("  el.change();");
//        js.append("})();");
//        
//        jss.addScript(js.toString(), element.getClientId());
    }
}
