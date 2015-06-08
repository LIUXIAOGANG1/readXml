package net.sourceforge.powerswing.tabbedpane.ui;

import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI;

public class WindowsPTabbedPaneReverseIconUI extends WindowsTabbedPaneUI implements CloseIconUI {

    private HashMap <Integer, Rectangle> iconRects = new HashMap <Integer, Rectangle> ();
    
    private static final ImageIcon close = new ImageIcon(WindowsPTabbedPaneReverseIconUI.class.getResource("close.gif"));
    private static final ImageIcon closehighlighted = new ImageIcon(WindowsPTabbedPaneReverseIconUI.class.getResource("closehighlighted.gif"));
    
    protected void layoutLabel(int tabPlacement, FontMetrics metrics, int tabIndex, String title, Icon icon, Rectangle tabRect, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        super.layoutLabel(tabPlacement, metrics, tabIndex, title, icon, tabRect, iconRect, textRect, isSelected);
        
        int newIconX = (iconRect.x + textRect.width) + (textRect.x - (iconRect.x + iconRect.width));
        int newIconY = textRect.y;
        textRect.x = iconRect.x;
        textRect.y = iconRect.y;
        iconRect.x = newIconX;
        iconRect.y = newIconY;
        iconRects.put(tabIndex, new Rectangle(iconRect.x, iconRect.y, iconRect.width, iconRect.height));
    }
    
    public Rectangle getIconBounds(int theTabIndex){
        return iconRects.get(theTabIndex);
    }
    
    public ImageIcon getCloseIconNotHighlighted(){
        return close;
    }
    
    public ImageIcon getCloseIconHighlighted(){
        return closehighlighted;
    }
    
    protected void setRolloverTab(int index) {
        try{
            super.setRolloverTab(index);
        }
        catch(ArrayIndexOutOfBoundsException e){}
    }
    
    protected Rectangle getTabBounds(int tabIndex, Rectangle dest) {
        try{
            return super.getTabBounds(tabIndex, dest);
        }
        catch (ArrayIndexOutOfBoundsException e){
            return new Rectangle();
        }
    }
}
