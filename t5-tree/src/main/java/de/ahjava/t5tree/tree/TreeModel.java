package de.ahjava.t5tree.tree;

import java.util.List;


public interface TreeModel<T> {
    List<T> getRootNodes();
    
    String getLabel(T node);
    
    String getId(T node);
    T fromId(String nodeId);

    boolean isLeaf(T node);
    List<T> getChildren(T node);
    
    boolean isExpanded(T node);
    boolean isEagerlyTransferred(T node);
    
    /**
     * CSS class for the &lt;div&gt; containing the entire node including its children 
     */
    String getNodeClass(T node);
}
