package de.arnohaase.t5tree.mixins;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;


/**
 * This is a mix-in that suppresses the enter key for a given HTML component. It is particularly useful to
 *  prevent submission of a form when a user presses enter.
 */
@Import(library="SuppressEnterKey.js")
public class SuppressEnterKey {
    @InjectContainer private Field field;
    @Environmental private JavaScriptSupport jss;

    public void afterRender(MarkupWriter writer) {
        jss.addScript("$j('#%s').suppressEnterKey();", field.getClientId());
    }
}
