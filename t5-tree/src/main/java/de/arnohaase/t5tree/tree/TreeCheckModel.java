package de.arnohaase.t5tree.tree;

import java.util.Set;


public interface TreeCheckModel {
    boolean isChecked(String nodeId);
    void setChecked(String nodeId, boolean checked);
    
    Set<String> getAllSelectedIds();
}
