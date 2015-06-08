package net.sourceforge.jpowergraph.swing.viewcontrols;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * @author Mick Kerrigan
 *
 * Created on 05-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/swing/src/net/sourceforge/jpowergraph/swing/viewcontrols/AngleListCellRenderer.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:37:12 $
 */

public class AngleListCellRenderer extends DefaultListCellRenderer {

    public AngleListCellRenderer() {
        super();
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof Integer && c instanceof JLabel){
            ((JLabel) c).setText(value + "°   ");
        }
        return c;
    }
}
