package de.arnohaase.t5tree.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.services.LibraryMapping;


public class T5TreeModule {
    
    public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration)  {
        configuration.add(new LibraryMapping("t5tree", "de.arnohaase.t5tree"));
    }
}
