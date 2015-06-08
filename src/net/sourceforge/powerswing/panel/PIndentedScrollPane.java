package net.sourceforge.powerswing.panel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JScrollPane;

public class PIndentedScrollPane extends JScrollPane{

    public PIndentedScrollPane(Component theView, int theTopIndent, int theBottomIndent, int theLeftindent, int theRightindent) {
        super(new PPanel(1, 1, 0, 0, new Object[]{
                "",     "0,1",
                "0,1",  theView
        }, theTopIndent, theBottomIndent, theLeftindent, theRightindent, Color.WHITE), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    
    public PIndentedScrollPane(Component theView, int theTopIndent, int theBottomIndent, int theLeftindent, int theRightindent, int theVerticalScrollbarPolicy, int theHorizontalScrollbarPolicy ) {
        super(new PPanel(1, 1, 0, 0, new Object[]{
                "",     "0,1",
                "0,1",  theView
        }, theTopIndent, theBottomIndent, theLeftindent, theRightindent, Color.WHITE), theVerticalScrollbarPolicy, theHorizontalScrollbarPolicy);
    }

}
