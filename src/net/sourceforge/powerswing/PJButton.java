package net.sourceforge.powerswing;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

import net.sourceforge.powerswing.localization.PBundle;
import net.sourceforge.powerswing.localization.StringAndMnemonic;

public class PJButton extends JButton {

    private StringAndMnemonic sam;

    public PJButton(String theKey, boolean theOpensNewWindow, PBundle theMessages) {
        super();
        setText(theKey, theOpensNewWindow, theMessages);
    }
    
    public PJButton(String theKey, boolean theExpandsTheDialog, boolean theIsExpanded, PBundle theMessages) {
        super();
        setText(theKey, false, theExpandsTheDialog, theIsExpanded, theMessages);
    }
    
    public PJButton(String theKey, Icon theImage, boolean theOpensNewWindow, PBundle theMessages, int theTextAlignment) {
        super(theImage);
        super.setHorizontalTextPosition(theTextAlignment);
        setText(theKey, theOpensNewWindow, theMessages);
    }
    
    public PJButton(Icon theImage, boolean focusPainted, boolean raiseOnMouseOver, boolean borderPainted) {
        super(theImage);
        
        super.setBorderPainted(borderPainted);
        super.setRolloverEnabled(true);
        super.setFocusPainted(focusPainted);
        if (borderPainted){
            super.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(1, 3, 1, 3)));
        }
        else{
            super.setBorder(BorderFactory.createRaisedBevelBorder());
        }
        
        if (raiseOnMouseOver){
            super.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent mouseEvent) {
                    JButton aButton = (JButton) mouseEvent.getSource();
                    if (aButton.isEnabled()) {
                        aButton.setBorderPainted(true);
                    }
                }
                public void mouseExited(MouseEvent mouseEvent) {
                    JButton aButton = (JButton) mouseEvent.getSource();
                    aButton.setBorderPainted(false);
                }
            });
        }
    }

    /**
     * @deprecated
     */
    public void setText(String text) {
        throw new UnsupportedOperationException();
    }
    
    public void setText(String theKey, boolean theOpensNewWindow, PBundle theMessages) {
        setText(theKey, theOpensNewWindow, false, false, theMessages);
    }
    
    public void setText(String theKey, boolean theExpandsTheDialog, boolean theIsExpanded, PBundle theMessages) {
        setText(theKey, false, theExpandsTheDialog, theIsExpanded, theMessages);
    }
    
    public void setText(String theKey, boolean theOpensNewWindow, boolean theExpandsTheDialog, boolean theIsExpanded, PBundle theMessages) {
        sam = theMessages.getStringAndMnemonic(theKey);
        
        String text = sam.getString();
        if (theOpensNewWindow){
            text += "...";
        }
        else if (theExpandsTheDialog){
            if (theIsExpanded){
                text += " <<";
            }
            else{
                text += " >>";
            }
        }
        super.setText(text);
        
        if (sam.getMneumonic() != (char) -1){
            super.setMnemonic(sam.getMneumonic());
            super.setDisplayedMnemonicIndex(sam.getMneumonicIndex());            
        }
    }
    
    public StringAndMnemonic getStringAndMneumoic(){
        return sam;   
    }
}