package net.sourceforge.powerswing.menu;


import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
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
public class PJMenuItem extends JMenuItem{

    public PJMenuItem(Action theAction, String theText, boolean theOpensNewWindow) {
        super(theAction);
        
        String text = theText;
        if (theOpensNewWindow){
            text += "...";
        }
        super.setText(text);
    }

    public PJMenuItem(Action theAction, String theKey, boolean theOpensNewWindow, PBundle theMessages) {
        super(theAction);
        setText(theKey, theOpensNewWindow, theMessages);
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
        else if (accelerator.equals("F1")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        }
        else if (accelerator.equals("SHIFT-F1")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.SHIFT_MASK));
        }
        else if (accelerator.equals("F2")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        }
        else if (accelerator.equals("SHIFT-F2")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, KeyEvent.SHIFT_MASK));
        }
        else if (accelerator.equals("F3")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        }
        else if (accelerator.equals("SHIFT-F3")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, KeyEvent.SHIFT_MASK));
        }
        else if (accelerator.equals("F4")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
        }
        else if (accelerator.equals("SHIFT-F4")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.SHIFT_MASK));
        }
        else if (accelerator.equals("F5")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        }
        else if (accelerator.equals("SHIFT-F5")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, KeyEvent.SHIFT_MASK));
        }
        else if (accelerator.equals("F6")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
        }
        else if (accelerator.equals("SHIFT-F6")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, KeyEvent.SHIFT_MASK));
        }
        else if (accelerator.equals("F7")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
        }
        else if (accelerator.equals("SHIFT-F7")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, KeyEvent.SHIFT_MASK));
        }
        else if (accelerator.equals("F8")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
        }
        else if (accelerator.equals("SHIFT-F8")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, KeyEvent.SHIFT_MASK));
        }
        else if (accelerator.equals("F9")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
        }
        else if (accelerator.equals("SHIFT-F9")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, KeyEvent.SHIFT_MASK));
        }
        else if (accelerator.equals("F10")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
        }
        else if (accelerator.equals("SHIFT-F10")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, KeyEvent.SHIFT_MASK));
        }
        else if (accelerator.equals("F11")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
        }
        else if (accelerator.equals("SHIFT-F11")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, KeyEvent.SHIFT_MASK));
        }
        else if (accelerator.equals("F12")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
        }
        else if (accelerator.equals("SHIFT-F12")){
            super.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, KeyEvent.SHIFT_MASK));
        }
        else{
            char acceleratorChar = theMessages.getChar(theKey + ".Accelerator");
            if (acceleratorChar != '-'){
                super.setAccelerator(KeyStroke.getKeyStroke(acceleratorChar, KeyEvent.CTRL_MASK));
            }
        }
    }
}