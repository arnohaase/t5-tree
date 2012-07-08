package de.arnohaase.t5treedemo.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.got5.tapestry5.jquery.JQuerySymbolConstants;

import de.arnohaase.t5treedemo.person.PersonService;
import de.arnohaase.t5treedemo.person.PersonServiceImpl;



public class T5TreeDemoModule {
    public static void bind(ServiceBinder serviceBinder) {
        serviceBinder.bind(PersonService.class, PersonServiceImpl.class);
    }

    public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
        configuration.add(JQuerySymbolConstants.SUPPRESS_PROTOTYPE, "false");
    }

    public static void contributeJavaScriptStackSource(MappedConfiguration<String, JavaScriptStack> configuration) {
        configuration.addInstance("bootstrap", BootstrapStack.class);
    }
}
