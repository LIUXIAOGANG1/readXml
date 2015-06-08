package net.sourceforge.powerswing.preferences.panel;

import java.util.HashMap;
import java.util.Set;

import net.sourceforge.powerswing.preferences.Preferences;

/**
 * 
 * 
 * @author mkerrigan2
 */
public class PreferencePanelRegistry {

    private HashMap <Integer, PreferencePanel> panels = new HashMap <Integer, PreferencePanel> ();
    
    public void register(int theIdentifier, PreferencePanel thePreferencePanel){
        panels.put(theIdentifier, thePreferencePanel);
    }
    
    public PreferencePanel get(int theIdentifier){
        return panels.get(theIdentifier);
    }
    
    public void deregister(int theIdentifier){
        panels.remove(theIdentifier);
    }

    public Set <Integer> getIds() {
        return panels.keySet();
    }
}
