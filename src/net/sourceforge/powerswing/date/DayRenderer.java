package net.sourceforge.powerswing.date;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class DayRenderer extends DefaultTableCellRenderer {

	public DayRenderer() {
		super();
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    if (row == 0){
	        c.setForeground(Color.WHITE);
            c.setBackground(Color.GRAY);
	    }
	    else{
			if (isSelected && table.getSelectedColumn() == column) {
	            c.setForeground(table.getSelectionForeground());
	            c.setBackground(table.getSelectionBackground());
			}
			else {
	            c.setForeground(table.getForeground());
	            c.setBackground(table.getBackground());
			}
	    }
	    String text = c.getText();
	    if (text.length() == 1){
	        c.setText("   " + text + " ");
	    }
	    else if (text.length() == 2){
	        c.setText("  " + text + " ");
	    }
	    c.setHorizontalTextPosition(SwingConstants.CENTER);
        
		return c;
	}
}