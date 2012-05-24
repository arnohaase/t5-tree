package de.ahjava.t5tree.tree;


public interface TreeExpansionModel {
    boolean isExpanded(String nodeId);
    void setExpanded(String nodeId, boolean expanded);
}
