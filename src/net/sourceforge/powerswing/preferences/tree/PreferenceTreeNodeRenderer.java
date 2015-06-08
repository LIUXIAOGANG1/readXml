package net.sourceforge.powerswing.preferences.tree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class PreferenceTreeNodeRenderer extends DefaultTreeCellRenderer {
    
    private Color fgColor = null;
    private Color bgColor = null;
    private Color borderColor = null;
    
    public PreferenceTreeNodeRenderer() {
        super();
        fgColor = super.getTextSelectionColor();
        bgColor = super.getBackgroundSelectionColor();
        borderColor = super.getBorderSelectionColor();
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        
        PreferenceTreeNode aNode = (PreferenceTreeNode) value;
        
        this.setIcon(null);
        this.setDisabledIcon(null);
        this.setText(aNode.getLocalizedText());
        
        if (tree.isEnabled()){
	        if (bgColor != null && fgColor != null && selected){
	            if (hasFocus){
	                this.setForeground(fgColor);
	                this.setBackgroundSelectionColor(bgColor);
	            }
	            else{
	                this.setForeground(super.getTextNonSelectionColor());
	                this.setBackgroundSelectionColor(new JLabel().getBackground());
	            }
	        }
	        
	        if (borderColor != null){
	            this.setBorderSelectionColor(borderColor);
	        }
        }
        else if(selected){
            this.setForeground(super.getTextNonSelectionColor());
            this.setBackgroundSelectionColor(new JLabel().getBackground());
            this.setBorderSelectionColor(new JLabel().getBackground());
        }
        
        this.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
        
        return this;
    }

    public void setSelectedNodeColor(Color theForegroundColor, Color theBackgroundColor){
        fgColor = theForegroundColor;
        bgColor = theBackgroundColor;
    }
    
    public void setBorderColor(Color theBorderColor){
        borderColor = theBorderColor;
    }
}