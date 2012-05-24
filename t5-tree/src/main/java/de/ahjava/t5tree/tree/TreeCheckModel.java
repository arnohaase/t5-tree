package de.ahjava.t5tree.tree;


public interface TreeCheckModel {
    boolean isChecked(String nodeId);
    void setChecked(String nodeId, boolean checked);
}
