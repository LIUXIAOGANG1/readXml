package net.sourceforge.powerswing.menu;


import javax.swing.JMenu;

import net.sourceforge.powerswing.localization.PBundle;
import net.sourceforge.powerswing.localization.StringAndMnemonic;

/**
 * This class is a subclass of JMenu and is used to create a JMenu
 * which has a name, mneumonic and menumonic index from the specified 
 * resource bundle.
 * 
 * @author mkerrigan
 */
public class PJMenu extends JMenu{

    /**
     * The PJMenu Constructor
     * 
     * @param theKey
     * @param messages
     */
    public PJMenu(String theKey, PBundle theMessages) {
        super();
        setText(theKey, theMessages);
    }
    
    public void setText (String theKey, PBundle theMessages) {
        StringAndMnemonic sam = theMessages.getStringAndMnemonic(theKey);
        super.setText(sam.getString());
        
        if (sam.getMneumonic() != (char) -1){
            super.setMnemonic(sam.getMneumonic());
            super.setDisplayedMnemonicIndex(sam.getMneumonicIndex());            
        }
    }
}
