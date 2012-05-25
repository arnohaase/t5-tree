package de.ahjava.t5treedemo.pages;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.Import;

import de.ahjava.t5tree.tree.TreeModel;


@Import(stylesheet="FileTree.css")
public class FileTree {
    public TreeModel<File> getFileModel() {
        return new TreeModel<File>() {
            private final char fileSeparator = System.getProperty("file.separator").charAt(0);

            @Override
            public List<File> getRootNodes() {
                return Arrays.asList(new File(".."));
            }

            @Override
            public String getLabel(File node) {
                return node.getName();
            }
            
            @Override
            public String getId(File node) {
                return node.getPath();
            }

            @Override
            public File fromId(String nodeId) {
                return new File(nodeId);
            }
            
            @Override
            public boolean isLeaf(File node) {
                return node.isFile();
            }

            private int getLevel(File node) {
                int result = 0;
                for (char ch: node.getPath().toCharArray()) {
                    if (ch == fileSeparator) {
                        result++;
                    }
                }
                return result;
            }
            
            @Override
            public List<File> getChildren(File node) {
                return Arrays.asList(node.listFiles());
            }

            @Override
            public boolean isInitiallyExpanded(File node) {
                return getLevel(node) < 2; // || getLevel(node) % 2 == 1;
            }

            @Override
            public boolean isInitiallyChecked(File node) {
                return false;
            }

            @Override
            public boolean isEagerlyTransferred(File node) {
                return getLevel(node) %2 != 0;
            }

            @Override
            public String getNodeClass(File node) {
                return null;
            }

            @Override
            public String getIconOpenClosedCommonClass(File node) {
                return null;
            }

            @Override
            public String getIconOpenClass(File node) {
                return null;
            }

            @Override
            public String getIconClosedClass(File node) {
                return null;
            }

            @Override
            public String getIconEmptyClass(File node) {
                return null;
            }
            
            @Override
            public String getChildrenDivClass(File node) {
                return null;
            }

            @Override
            public String getNodeRowClass(File node) {
                return null;
            }

            @Override
            public String getLeafClass(File node) {
                return null;
            }

            @Override
            public String getIconCompartmentClass(File node) {
                return null;
            }

            @Override
            public Asset getIconUrl(File node) {
                return null;
            }
        };
    }
}
