package net.sourceforge.powerswing.tabbedpane.listener;

import java.awt.Component;
import java.awt.event.MouseEvent;

public class PTabbedPaneMouseEvent extends MouseEvent {

    private int tabIndex;
    private boolean inIcon;
    
    public PTabbedPaneMouseEvent(MouseEvent e, int theTabIndex, boolean theInIcon) {
        this((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton(), theTabIndex, theInIcon);
    }
    
    public PTabbedPaneMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger, int theTabIndex, boolean theInIcon) {
        this(source, id, when, modifiers, x, y, clickCount, popupTrigger, NOBUTTON, theTabIndex, theInIcon);
    }
    
    public PTabbedPaneMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger, int button, int theTabIndex, boolean theInIcon) {
        super(source, id, when, modifiers, x, y, clickCount, popupTrigger, button);
        tabIndex = theTabIndex;
        inIcon = theInIcon;
    }
    
    public int getTabIndex(){
        return tabIndex; 
    }
    
    public boolean inIcon(){
        return inIcon; 
    }
}
