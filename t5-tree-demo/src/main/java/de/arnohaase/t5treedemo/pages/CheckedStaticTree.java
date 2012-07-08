package de.arnohaase.t5treedemo.pages;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;


public class CheckedStaticTree {
    @Property private boolean checked1;
    @Property private boolean checked2;
    @Property private boolean checked3;

    @Property private boolean open1;
    @Property private boolean open2;
    @Property private boolean open3;

    @InjectComponent private Zone zone;
    
    public void onActivate() {
        open1 = true;
        open2 = true;
    }
    
    public String getNumChecked() {
        int result = 0;
        if (checked1) {
            result++;
        }
        if(checked2) {
            result++;
        }
        if(checked3) {
            result++;
        }
        return String.valueOf(result);
    }

    public String getNumExpanded() {
        int result = 0;
        if (open1) {
            result++;
        }
        if(open2) {
            result++;
        }
        if(open3) {
            result++;
        }
        return String.valueOf(result);
    }
    
    public Object onSubmit() {
        return zone;
    }
}
