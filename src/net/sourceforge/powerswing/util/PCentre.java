package net.sourceforge.powerswing.util;

import java.awt.Component;
import java.awt.Dimension;

/**
 * @author Niall O Cuilinn
 *
 * Created on Sep 18, 2003
 */
public class PCentre {

    /**
     * Centres a component on the screen
     * @author Niall O Cuilinn
     * 
     * @param theComponent the component to be centred
     */
    public static void centreRelativeToScreen(Component theComponent){
        if (theComponent == null){
            return;
        }
        Dimension dim = theComponent.getToolkit().getScreenSize();
        theComponent.setLocation((dim.width-theComponent.getWidth())/2,(dim.height-theComponent.getHeight())/2);
    }
    
    /**
     * Centres a component relative to the position of its parent.
     * If this position would result in part of the component being
     * off the screen then the component is centred on the screen instead
     * @author Niall O Cuilinn
     * 
     * @param theComponent the component to be centred
     */
    public static void centreRelativeToParent(Component theComponent, Component theParent){
        if (theComponent == null){
            return;
        }
        if (theParent == null){
            centreRelativeToScreen(theComponent);
            return;
        }
        centreRelativeToParent(theComponent, theParent, true);
    }
    
    /**
     * Centres a component relative to the position of its parent.
     * If this position would result in part of the component being
     * off the screen then the component is centred on the screen 
     * instead if reposition is true
     * @author Niall O Cuilinn
     * 
     * @param theComponent the component to be centred
     * @param theparent the component to be centred against
     * @param reposition whether to reposition relative to screen if part of the component goes off screeen
     */
    public static void centreRelativeToParent(Component theComponent, Component theParent, boolean reposition){
        if (theComponent == null){
            return;
        }
        if (theParent == null){
            centreRelativeToScreen(theComponent);
            return;
        }
        
        Dimension dim = theComponent.getToolkit().getScreenSize();
        int x = theParent.getX()+((theParent.getWidth()-theComponent.getWidth())/2);
        int y = theParent.getY()+((theParent.getHeight()-theComponent.getHeight())/2);
        if((x + theComponent.getWidth() > dim.width || y + theComponent.getHeight() > dim.height || x < 0 || y < 0) && reposition) {
            centreRelativeToScreen(theComponent);
        }
        else {
            theComponent.setLocation(x,y);
        }
    }
}