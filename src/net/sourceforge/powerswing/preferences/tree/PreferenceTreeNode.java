package net.sourceforge.powerswing.preferences.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.powerswing.localization.PBundle;

public class PreferenceTreeNode extends DefaultMutableTreeNode {

    private String key;
    private PBundle messages;
    private int id;
    
    public PreferenceTreeNode(String theBundleKey, PBundle theMessages, int thePanelId){
        super();
        this.key = theBundleKey;
        this.id = thePanelId;
        this.messages = theMessages;
    }
    
    public int getId(){
        return id;
    }

    public String getLocalizedText() {
        if (key == null){
            return null;
        }
        return messages.getString(key);
    }

    public String getLocalizedTextIncludingParent() {
        PreferenceTreeNode parent = (PreferenceTreeNode) getParent();
        
        String myText = getLocalizedText();
        if (parent == null){
            return myText;
        }
        
        String parentText = parent.getLocalizedTextIncludingParent();
        if (parentText == null){
            return myText;
        }
        return parentText + " - " + myText;
    }
    
    public PreferenceTreeNode getChildAt(int index) {
        return (PreferenceTreeNode) super.getChildAt(index);
    }
}