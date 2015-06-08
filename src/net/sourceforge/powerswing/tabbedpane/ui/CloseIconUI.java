package net.sourceforge.powerswing.tabbedpane.ui;

import java.awt.Rectangle;

import javax.swing.ImageIcon;

public interface CloseIconUI {
    
    public Rectangle getIconBounds(int theTabIndex);
    public ImageIcon getCloseIconNotHighlighted();
    public ImageIcon getCloseIconHighlighted();
}
