package net.sourceforge.powerswing.toolbar;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;

import net.sourceforge.powerswing.localization.PBundle;

/**
 * This class is a subclass of JToolBar and is used to create a JToolBar for
 * the top of an application which has images on it. It adds a tooltip from
 * the specified Resource Bundle. It also forces some GUI attributes.
 * 
 * @author mkerrigan
 */
public class PJToolBar extends JToolBar{

    private boolean tight;

    /**
     * PJToolbar constructor
     */
    public PJToolBar(boolean theTight) {
        super();
        this.tight = theTight;
    }
    
    /**
     * The add method used to add a Button to the ToolBar
     * 
     * @param theAction
     * @param theKey
     * @param messages
     * @return JButton
     */
    public JButton add(Action theAction, String theKey, PBundle messages) {
        JButton button = super.add(theAction);
        button.setToolTipText(messages.getString(theKey + ".ToolTip"));
        
        if (!tight){
            button.setBorderPainted(true);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(), BorderFactory.createEmptyBorder(5, 6, 5, 6)));
        }
        else{
            button.setBorderPainted(false);
            button.setRolloverEnabled(true);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(1, 2, 1, 2)));
        }
        
        button.addMouseListener(new MouseAdapter() {
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
        
        return button;
    }

    /** 
     * Unsupported Operation
     */
    public JButton add(Action a) {
        throw new UnsupportedOperationException();
    }
}