package net.sourceforge.powerswing.preferences;

import java.util.HashMap;
import java.util.Set;

public class Preferences {
    
    private HashMap <String, Preference> prefs = new HashMap <String, Preference> ();
    
    public void register(String theId, Preference thePreference){
        prefs.put(theId, thePreference);
    }
    
    public Preference get(String theIdentifier){
        return prefs.get(theIdentifier);
    }
    
    public void deregister(String theIdentifier){
        prefs.remove(theIdentifier);
    }

    public Set <String> getIds() {
        return prefs.keySet();
    }
    
    public void write(){
        for (Preference p : prefs.values()) {
            p.write();
        }
    }
}
