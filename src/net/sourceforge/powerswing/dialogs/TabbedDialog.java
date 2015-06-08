package net.sourceforge.powerswing.dialogs;


import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sourceforge.powerswing.PJButton;
import net.sourceforge.powerswing.localization.PBundle;
import net.sourceforge.powerswing.panel.PPanel;
import net.sourceforge.powerswing.util.PCentre;

public class TabbedDialog extends JDialog{

    public TabbedDialog(JFrame theParent, PBundle theMessages, String theTitle, int theTabPosition, String[] thePanelNames, Component[] thePanels) {
        super(theParent, theTitle);
        init(theMessages, theTabPosition, thePanelNames, thePanels);
    }
    
    public TabbedDialog(JDialog theParent, PBundle theMessages, String theTitle, int theTabPosition, String[] thePanelNames, Component[] thePanels) {
        super(theParent, theTitle);
        init(theMessages, theTabPosition, thePanelNames, thePanels);
    }

    public void init(PBundle theMessages, int theTabPosition, String[] thePanelNames, Component[] thePanels){
        PJButton close = new PJButton("Button.Close", false, theMessages);
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        
        JTabbedPane jtp = new JTabbedPane(theTabPosition);
        for (int i = 0; i < thePanelNames.length; i++) {
            jtp.addTab(thePanelNames[i], thePanels[i]);
        }
       
        PPanel top = new PPanel(1, 1, 0, 0, new Object[]{
                "", 	"0,1",
                "0,1",	jtp
        }, 5, 5, 5, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(close);
        
        PPanel main = new PPanel(2, 1, 0, 0, new Object[]{
                "", 	"0,1",
                "0,1",	top,
                "0", 	buttonPanel
        }, 0, 0, 0, 0);
        this.add(main);
        this.pack();
        PCentre.centreRelativeToParent(this, this.getParent());
    }

    protected void close() {
        this.setVisible(false);
    }
}