package net.sourceforge.jpowergraph.swing.manipulator;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.manipulator.popup.ToolTipListener;

/**
 * @author Mick Kerrigan
 *
 * Created on 03-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/swing/src/net/sourceforge/jpowergraph/swing/manipulator/DefaultSwingToolTipListener.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:37:12 $
 */

public class DefaultSwingToolTipListener implements ToolTipListener <JComponent, Color>{

    public boolean addNodeToolTipItems(Node theNode, JComponent theJComponent, Color backgroundColor) {
        theJComponent.setLayout(new BorderLayout());
        JEditorPane editor = new JEditorPane("text/html", "<font size=3><b>" + theNode.getLabel().replaceAll("\n", "<br>") + "</b></font><hr size=1><font size=3><b>Type: </b>" + theNode.getNodeType() + "</font>");
        editor.setBackground(new Color(255, 255, 204));
        editor.setEditable(false);
        theJComponent.add(editor);
        return true;
    }
    
    public void removeNodeToolTipItems(Node theNode, JComponent theJComponent) {
        theJComponent.removeAll();
    }
}
