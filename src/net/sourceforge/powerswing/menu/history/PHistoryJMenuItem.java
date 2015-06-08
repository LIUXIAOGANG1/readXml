package net.sourceforge.powerswing.menu.history;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * This class a subclass of JMenuItem and is used to display a filename, 
 * which can be reopened by the user. The filename is specified is cleaned
 * up and reduced in size for display on the JMenu which it is placed on.
 * 
 * @author mkerrigan
 */
public class PHistoryJMenuItem extends JMenuItem{

    /**
     * The constructor for PHistoryJMenuItem
     * 
     * @param theAction
     * @param theFileName
     * @param theIndex
     */
    public PHistoryJMenuItem (final Action theAction, final String theFileName, int theIndex){ 
        int numSlashes = 0;
        for (int j = 0; j < theFileName.length(); j++){
            if (theFileName.charAt(j) == File.separatorChar){
                numSlashes++;
            }
        }
        
        String displayName;
        if (numSlashes < 3){
            displayName = theFileName;
        }
        else{
            String filename = theFileName.substring(theFileName.lastIndexOf(File.separatorChar));
            String beforeFilename = theFileName.substring(0, theFileName.lastIndexOf(File.separatorChar));
            String lastdir = beforeFilename.substring(beforeFilename.lastIndexOf(File.separatorChar));
            String firstbit = theFileName.substring(0, theFileName.indexOf(File.separatorChar));
            displayName = firstbit + File.separator + "..." + lastdir + filename;   
        }
        
        displayName = "" + (theIndex)  + ". " + displayName;
        
        Action thisAction = new AbstractAction(displayName, (Icon) theAction.getValue(Action.SMALL_ICON)) {
            public void actionPerformed(ActionEvent e) {
                theAction.actionPerformed(null);
            }
        };
        
        super.setAction(thisAction);
        
        char mnemChar = (new String("" + (theIndex))).charAt(0);
        super.setMnemonic(mnemChar);
        super.setToolTipText(theFileName);
    }
}