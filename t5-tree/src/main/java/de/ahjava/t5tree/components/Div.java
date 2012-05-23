package de.ahjava.t5tree.components;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;


@SupportsInformalParameters
public class Div implements ClientElement {
    private String clientId;

    private Element divElement;
    
    @Environmental private JavaScriptSupport jsSupport;

    @Inject private ComponentResources resources;

    @BeginRender
    void beginRender(MarkupWriter writer) {
        clientId = null;

        divElement = writer.element("div");
        resources.renderInformalParameters(writer);
        resources.getBody();
    }

    void afterRender(MarkupWriter writer) {
        writer.end();
    }
    
    public String getClientId() {
        if (clientId == null) {
            clientId = jsSupport.allocateClientId(resources);
            divElement.forceAttributes("id", clientId);
        }

        return clientId;
    }
}
