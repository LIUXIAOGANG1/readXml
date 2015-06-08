package net.sourceforge.powerswing.preferences.tree;

import javax.swing.tree.DefaultTreeModel;

public class PreferenceTreeModel extends DefaultTreeModel {

    public PreferenceTreeModel(PreferenceTreeNode root) {
        super(root);
    }
    
    public PreferenceTreeNode getRootNode(){
        return (PreferenceTreeNode) getRoot();
    }
}