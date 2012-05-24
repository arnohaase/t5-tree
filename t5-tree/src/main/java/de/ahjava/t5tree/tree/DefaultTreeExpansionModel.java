package de.ahjava.t5tree.tree;

import java.util.Set;

import org.apache.tapestry5.ioc.internal.util.CollectionFactory;


public class DefaultTreeExpansionModel implements TreeExpansionModel {
    private final Set<String> expandedNodes = CollectionFactory.newSet();

    private static class LazyInitExpansionModel<T> extends DefaultTreeExpansionModel {
        private final TreeModel<T> model;
        private final Set<String> initializedNodes = CollectionFactory.newSet();
        
        public LazyInitExpansionModel(TreeModel<T> model) {
            this.model = model;
        }
        
        @Override
        public boolean isExpanded(String nodeId) {
            if (!initializedNodes.contains(nodeId)) {
                setExpanded(nodeId, model.isInitiallyExpanded(model.fromId(nodeId)));
            }
            return super.isExpanded(nodeId);
        }
        
        @Override
        public void setExpanded(String nodeId, boolean expanded) {
            initializedNodes.add(nodeId);
            super.setExpanded(nodeId, expanded);
        }
    }
    
    public static <T> TreeExpansionModel createFrom(TreeModel<T> model) {
        return new LazyInitExpansionModel<T>(model);
    }
    
    @Override
    public boolean isExpanded(String nodeId) {
        return expandedNodes.contains(nodeId);
    }

    @Override
    public void setExpanded(String nodeId, boolean expanded) {
        if (expanded) {
            expandedNodes.add(nodeId);
        }
        else {
            expandedNodes.remove(nodeId);
        }
    }
}
