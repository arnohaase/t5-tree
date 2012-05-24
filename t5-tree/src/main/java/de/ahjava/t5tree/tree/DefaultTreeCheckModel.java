package de.ahjava.t5tree.tree;

import java.util.Set;

import org.apache.tapestry5.ioc.internal.util.CollectionFactory;


public class DefaultTreeCheckModel implements TreeCheckModel {
    private final Set<String> checkedNodes = CollectionFactory.newSet();

    private static class LazyInitCheckModel<T> extends DefaultTreeCheckModel {
        private final TreeModel<T> model;
        private final Set<String> initializedNodes = CollectionFactory.newSet();
        
        public LazyInitCheckModel(TreeModel<T> model) {
            this.model = model;
        }
        
        @Override
        public boolean isChecked(String nodeId) {
            if (!initializedNodes.contains(nodeId)) {
                setChecked(nodeId, model.isInitiallyChecked(model.fromId(nodeId)));
            }
            return super.isChecked(nodeId);
        }
        
        @Override
        public void setChecked(String nodeId, boolean checked) {
            initializedNodes.add(nodeId);
            super.setChecked(nodeId, checked);
        }
    }
    
    public static <T> TreeCheckModel createFrom(TreeModel<T> model) {
        return new LazyInitCheckModel<T>(model);
    }
    
    @Override
    public boolean isChecked(String nodeId) {
        return checkedNodes.contains(nodeId);
    }

    @Override
    public void setChecked(String nodeId, boolean checked) {
        if (checked) {
            checkedNodes.add(nodeId);
        }
        else {
            checkedNodes.remove(nodeId);
        }
    }
}
