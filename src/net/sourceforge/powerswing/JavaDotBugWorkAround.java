package net.sourceforge.powerswing;

import java.awt.Font;
import java.util.Iterator;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class JavaDotBugWorkAround {
    static public void workaround() {
        UIDefaults uid = UIManager.getLookAndFeel().getDefaults();
        
        for (Iterator i = uid.keySet().iterator(); i.hasNext();) {
            String s = (String) i.next();
            if (s.endsWith("font") && ((FontUIResource) uid.get(s)).getName().equals("Microsoft Sans Serif")) {
                FontUIResource curFont = (FontUIResource) uid.get(s);
                FontUIResource newFont = new FontUIResource(new Font("Arial", curFont.getStyle(), curFont.getSize()));
                UIManager.put(s, newFont);
            }
        }
   }
}