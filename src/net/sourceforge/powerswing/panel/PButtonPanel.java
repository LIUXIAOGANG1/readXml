package net.sourceforge.powerswing.panel;


import java.awt.FlowLayout;

import javax.swing.JPanel;

import net.sourceforge.powerswing.PJButton;

public class PButtonPanel extends JPanel {

    public static final int LEFT = FlowLayout.LEFT;
    public static final int RIGHT = FlowLayout.RIGHT;
    
    public PButtonPanel(PJButton[] buttons) {
        super(new FlowLayout(RIGHT));
        for (int i = 0; i < buttons.length; i++) {
            this.add(buttons[i]);
        }
    }
    
    public PButtonPanel(PJButton[] buttons, int direction) {
        super(new FlowLayout(direction));
        for (int i = 0; i < buttons.length; i++) {
            this.add(buttons[i]);
        }
    }
}
