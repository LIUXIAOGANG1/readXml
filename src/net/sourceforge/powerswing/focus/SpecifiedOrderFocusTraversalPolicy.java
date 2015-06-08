package net.sourceforge.powerswing.focus;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;
import java.util.ArrayList;

/**
 * This class is used to specify the order you want child items in Component
 * to tabbed through on a GUI.
 * 
 * @see java.awt.FocusTraversalPolicy
 * 
 * @author Mick Kerrigan
 * @version $Id$
 * Copyright Changing Worlds Limited 2002
 */
public class SpecifiedOrderFocusTraversalPolicy extends FocusTraversalPolicy 
{    
    private Component[] c;
    private Component init;
    
    /**
     * Default Constructor for SpecifiedOrderFocusTraversalPolicy
     * 
     * @param theComponents An array of Components in the order they should be tabbed through
     */
    public SpecifiedOrderFocusTraversalPolicy(Component[] theComponents, Component theInitial)
    {
        this.c = theComponents;
        this.init = theInitial;
    }
    
    /**
     * Default Constructor for SpecifiedOrderFocusTraversalPolicy
     * 
     * @param theComponents An arraylist of Components in the order they should be tabbed through
     */
    public SpecifiedOrderFocusTraversalPolicy(ArrayList <Component> theComponents, Component theInitial)
    {
        this.c = theComponents.toArray(new Component[theComponents.size()]);
        this.init = theInitial;
    }
    
    /**
     * @see java.awt.FocusTraversalPolicy#getComponentAfter(Container, Component)
     */
    public Component getComponentAfter(Container focusCycleRoot, Component aComponent) 
    {
        int index = this.getIndex(aComponent);
        if (index != -1) {
            int pointer = -1;
            if (index == (c.length - 1)) {
                pointer = 0;
            }
            else {
                pointer = index + 1;
            }

            while (pointer != index) {
                if (c[pointer].isEnabled()) {
                    return c[pointer];
                }
                if (pointer == (c.length - 1)) {
                    pointer = 0;
                }
                else {
                    pointer++;
                }
            }
        }
        return null;
    }

    /**
     * @see java.awt.FocusTraversalPolicy#getComponentBefore(Container, Component)
     */
    public Component getComponentBefore(Container focusCycleRoot, Component aComponent) 
    {
        int index = this.getIndex(aComponent);
        if (index != -1) {
            int pointer = -1;
            if (index == 0) {
                pointer = c.length - 1;
            }
            else {
                pointer = index - 1;
            }
            while (pointer != index) {
                if (c[pointer].isEnabled()) {
                    return c[pointer];
                }
                if (pointer == 0) {
                    pointer = c.length - 1;
                }
                else {
                    pointer--;
                }
            }
        }
        return null;
    }
    
    /**
     * @see java.awt.FocusTraversalPolicy#getDefaultComponent(Container)
     */
    public Component getDefaultComponent(Container focusCycleRoot) 
    {
        return null;
    }
    
    /**
     * @see java.awt.FocusTraversalPolicy#getFirstComponent(Container)
     */
    public Component getFirstComponent(Container focusCycleRoot) 
    {
        return c[0];
    }
    
    /**
     * @see java.awt.FocusTraversalPolicy#getLastComponent(Container)
     */
    public Component getLastComponent(Container focusCycleRoot) 
    {
        return c[c.length - 1];
    }
    
    /**
     * @see java.awt.FocusTraversalPolicy#getInitialComponent(Window)
     */
    public Component getInitialComponent(Window window) 
    {
        return init;
    }

    /**
     * Method gets the Tab Index of the Component.
     * 
     * @param aComponent The Component whos Tab Index you want
     * 
     * @return int The Tab Index
     */
    private int getIndex(Component aComponent) 
    {
        int index = -1;
        for (int i = 0; i < c.length; i++) {
            Component thisComponent = c[i];
            if (thisComponent.equals(aComponent)) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("---------------------" + "\n");
        for (int i = 0; i < c.length; i++) {
            Component jc = c[i];
            sb.append("=> " + jc.getClass() + "\n");
        }
        sb.append("Starts With : " + init.getClass() + "\n");
        sb.append("---------------------" + "\n");
        return sb.toString();
    }
}
