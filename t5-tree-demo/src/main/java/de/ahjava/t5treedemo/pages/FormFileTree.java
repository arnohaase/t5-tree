package de.ahjava.t5treedemo.pages;

import java.io.File;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;

import de.ahjava.t5tree.tree.DefaultTreeCheckModel;
import de.ahjava.t5tree.tree.DefaultTreeExpansionModel;
import de.ahjava.t5tree.tree.TreeCheckModel;
import de.ahjava.t5tree.tree.TreeExpansionModel;
import de.ahjava.t5tree.tree.TreeModel;


@Import(stylesheet="FileTree.css")
public class FormFileTree {
    @InjectComponent private Zone zone;
    
    @Property private TreeModel<File> fileModel;
    
    @SuppressWarnings("unused")
    @Property private TreeExpansionModel expansionModel;
    
    @Property private TreeCheckModel checkModel;
    
    public void onActivate() {
        fileModel = createFileModel();
        expansionModel = DefaultTreeExpansionModel.createFrom(fileModel);
        checkModel = DefaultTreeCheckModel.createFrom(fileModel);
    }

    public Object onSubmit() {
        return zone;
    }

    public String getNumSelectedFiles() {
        return NumberFormat.getIntegerInstance().format(checkModel.getAllSelectedIds().size());
    }
    
    public String getTotalSelectedSize() {
        long result = 0;
        
        for (String fileId: checkModel.getAllSelectedIds()) {
            final File f = fileModel.fromId(fileId);
            result += f.length();
        }
        
        return NumberFormat.getIntegerInstance().format(result);
    }
    
    private static TreeModel<File> createFileModel() {
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
                return getLevel(node) < 1; // || getLevel(node) % 2 == 1;
            }

            @Override
            public boolean isInitiallyChecked(File node) {
                return node.getName().startsWith("."); 
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
            public String getIconEmptyClass(File node) {
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
        };
    }
}
