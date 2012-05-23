package de.ahjava.t5tree.tree;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


/**
 * This class keeps track of the hierarchy of the current tree in a ThreadLocal. It is maintained by the TreeNode component
 *  and should therefore not be modified by user code, but user code may make use of the data this class provides.
 */
public class TreeHierarchyTracker {
    private static final ThreadLocal<LinkedList<String>> parentCheckboxId = new ThreadLocal<LinkedList<String>>();
    
    public static void push(String checkboxId) {
        LinkedList<String> parents = parentCheckboxId.get();
        if (parents == null) {
            parents = new LinkedList<String>();
            parentCheckboxId.set(parents);
        }
        parents.add(checkboxId);
    }

    public static void pop() {
        final Deque<String> parents = parentCheckboxId.get();
        parents.removeLast();
        if(parents.isEmpty()) {
            parentCheckboxId.remove();
        }
    }
    
    public static String getParentCheckboxId() {
        if (parentCheckboxId.get() == null) {
            return null;
        }
        return parentCheckboxId.get().getLast();
    }
    
    public static List<String> getAncesterCheckboxIds() {
        if (parentCheckboxId.get() == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(parentCheckboxId.get());
    }
    
    public static String getCheckboxHierarchyCssClasses() {
        
        if (TreeHierarchyTracker.getParentCheckboxId() == null) {
            return "";
        }
        final StringBuilder result = new StringBuilder("child-of-" + getParentCheckboxId());

        for (String ancestor: TreeHierarchyTracker.getAncesterCheckboxIds()) {
            result.append (" descendant-of-" + ancestor);
        }
        return result.toString();
    }
}
