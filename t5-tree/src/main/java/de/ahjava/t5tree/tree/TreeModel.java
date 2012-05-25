package de.ahjava.t5tree.tree;

import java.util.List;


public interface TreeModel<T> {
    List<T> getRootNodes();
    
    String getLabel(T node);
    
    String getId(T node);
    T fromId(String nodeId);

    boolean isLeaf(T node);
    List<T> getChildren(T node);

    /**
     * used only for initial update and only if no explicit expansionModel is configured for a tree 
     */
    boolean isInitiallyExpanded(T node);
    
    boolean isInitiallyChecked(T node);
    
    boolean isEagerlyTransferred(T node);
    
    /**
     * Optional CSS class for the &lt;div&gt; containing the entire node including its children
     */
    String getNodeClass(T node);
    
    /**
     * Optional CSS class added to the open/close compartment, regardless whether it is open or closed. If non-null, overrides the tree default.
     */
    String getIconOpenClosedCommonClass(T node);

    /**
     * Optional CSS class added to the open/close compartment iff the node is expanded. If non-null, overrides the tree default.
     */
    String getIconOpenClass(T node);

    /**
     * Optional CSS class added to the open/close compartment iff the node is collapsed. If non-null, overrides the tree default.
     */
    String getIconClosedClass(T node);

    /**
     * Optional CSS class added to the open/close compartment iff the node is a folder but empty. If non-null, overrides the tree default.
     */
    String getIconEmptyClass(T node);
    
    /**
     * Optional CSS class for the children div. If non-null, overrides the tree default.
     */
    String getChildrenDivClass(T node);

    /**
     * Optional CSS class for a node itself. If non-null, overrides the tree default.
     */
    String getNodeRowClass(T node);

    /**
     * Optional CSS class for a leaf. If non-null, overrides the tree default.
     */
    String getLeafClass(T node);
}
