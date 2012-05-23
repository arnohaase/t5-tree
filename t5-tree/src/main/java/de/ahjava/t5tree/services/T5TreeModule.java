package de.ahjava.t5tree.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.services.LibraryMapping;


public class T5TreeModule {
    
    public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration)  {
        configuration.add(new LibraryMapping("t5tree", "de.ahjava.t5tree"));
    }
}
