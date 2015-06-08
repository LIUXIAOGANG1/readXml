package net.sourceforge.powerswing.preferences.tree;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class PreferenceTree extends JTree{

    private PreferenceTreeNodeRenderer preferenceTreeNodeRenderer;
    
    public PreferenceTree(PreferenceTreeNode theRoot){
        super(new PreferenceTreeModel(theRoot));
        this.preferenceTreeNodeRenderer = new PreferenceTreeNodeRenderer();
        this.preferenceTreeNodeRenderer.setBorderColor(preferenceTreeNodeRenderer.getBackgroundSelectionColor());
        this.setCellRenderer(preferenceTreeNodeRenderer);
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
        this.expandAll();
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }
    
    public PreferenceTreeNode getSelectedNode() {
        if (this.getSelectionPath() == null){
            return null;
        }
        return (PreferenceTreeNode) this.getSelectionPath().getLastPathComponent();
    }

    public void selectNodeWithPanelId(int thePreferencePanelId) {
        Enumeration e = ((PreferenceTreeNode) getModel().getRoot()).depthFirstEnumeration();
        while (e.hasMoreElements()){
            PreferenceTreeNode ptn = (PreferenceTreeNode) e.nextElement();
            if (ptn.getId() == thePreferencePanelId){
                this.getSelectionModel().setSelectionPath(new TreePath(((DefaultTreeModel) getModel()).getPathToRoot(ptn)));
                break;
            }
        }
    }

    public void expandAll() {
        Enumeration e = ((PreferenceTreeNode) getModel().getRoot()).depthFirstEnumeration();
        while (e.hasMoreElements()){
            this.expandPath(new TreePath(((DefaultTreeModel) getModel()).getPathToRoot((PreferenceTreeNode) e.nextElement())));
        }
    }
    
    public PreferenceTreeModel getModel(){
        return (PreferenceTreeModel) super.getModel();
    }
}
