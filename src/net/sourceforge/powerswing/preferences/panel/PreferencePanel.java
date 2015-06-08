package net.sourceforge.powerswing.preferences.panel;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import net.sourceforge.powerswing.preferences.Preferences;

/**
 * 
 * 
 * @author mkerrigan2
 */
public abstract class PreferencePanel extends JPanel {
    public abstract void init();
    public abstract void setContainer(Container theContainer);
    public abstract void setKeyListeners(KeyListener theEnterKeyListener, KeyListener theEscKeyListener);
    public abstract ArrayList <Component> getOrderedComponentList();
    
    public abstract void set(Preferences thePreferences);
    public abstract void update(Preferences thePreferences);
}
