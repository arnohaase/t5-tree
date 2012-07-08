package de.ahjava.t5treedemo.pages;

import org.apache.tapestry5.annotations.Property;


public class PersonDetails {
    @Property
    private long personId;
    
    public void onActivate(long personId) {
        this.personId = personId;
    }
}
