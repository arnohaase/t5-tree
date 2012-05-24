package de.ahjava.t5tree.tree;

import java.util.Set;

import org.apache.tapestry5.ioc.internal.util.CollectionFactory;


public class DefaultTreeExpansionModel implements TreeExpansionModel {
    private final Set<String> expandedNodes = CollectionFactory.newSet();

    public static <T> TreeExpansionModel initFrom(TreeModel<T> model) {
        final DefaultTreeExpansionModel result = new DefaultTreeExpansionModel();
        for (T rootNode: model.getRootNodes()) {
            initRec(model, result, rootNode);
        }
        return result;
    }
    
    private static <T> void initRec(TreeModel<T> model, DefaultTreeExpansionModel result, T node) {
        if (model.isLeaf(node)) {
            return;
        }
        result.setExpanded(model.getId(node), model.isExpanded(node));
        if (model.isEagerlyTransferred(node) || model.isExpanded(node)) {
            for (T child: model.getChildren(node)) {
                initRec(model, result, child);
            }
        }
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
