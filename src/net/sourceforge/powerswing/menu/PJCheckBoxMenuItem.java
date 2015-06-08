package net.sourceforge.powerswing.menu;


import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import net.sourceforge.powerswing.localization.PBundle;
import net.sourceforge.powerswing.localization.StringAndMnemonic;

/**
 * This class is a subclass of JMenuItem and is used to create a JMenuItem
 * with the correct mneumonic, menumonix index and accelerator from the resource
 * bundle specified. You should set the accelerator to the '-' charachter in the 
 * resource bundle if you do not want an accelerator character to be specified. 
 * 
 * @author mkerrigan
 */
public class PJCheckBoxMenuItem extends JCheckBoxMenuItem{

    /**
     * The PJCheckBoxMenuItem constructor
     * 
     * @param theAction
     * @param theKey
     * @param messages
     */
    public PJCheckBoxMenuItem(Action theAction, String theKey, boolean theOpensNewWindow, boolean theSelected, PBundle theMessages) {
        super(theAction);
        setText(theKey, theOpensNewWindow, theMessages);
        setSelected(theSelected);
    }
    
    public void setText(String theKey, boolean theOpensNewWindow, PBundle theMessages){
        StringAndMnemonic sam = theMessages.getStringAndMnemonic(theKey);
        
        String text = sam.getString();
        if (theOpensNewWindow){
            text += "...";
        }
        super.setText(text);
        
        if (sam.getMneumonic() != (char) -1){
            super.setMnemonic(sam.getMneumonic());
            super.setDisplayedMnemonicIndex(sam.getMneumonicIndex());            
        }
        
        String accelerator = theMessages.getString(theKey + ".Accelerator");
        if (accelerator.equals("DELETE")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        }
        else{
            char acceleratorChar = theMessages.getChar(theKey + ".Accelerator");
            if (acceleratorChar != '-'){
                super.setAccelerator(KeyStroke.getKeyStroke(acceleratorChar, KeyEvent.CTRL_MASK));
            }
        }
    }
}