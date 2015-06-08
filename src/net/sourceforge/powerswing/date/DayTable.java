package net.sourceforge.powerswing.date;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class DayTable extends JTable {

    private int lastGoodRow = -1;
    private int lastGoodColumn = -1;
    private boolean humanEditing = false;
    
    public DayTable() {
        super();
        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int row = getSelectedRow();
                int column = getSelectedColumn();
                if (humanEditing){
                    lastGoodRow = row;
                    lastGoodColumn = column;
                }
                else{
	                if (row == 0 || (row != -1 && column != -1 && ((String) getValueAt(row, column)).equals(""))){
	                    if (lastGoodRow != -1 && lastGoodColumn != -1){
		                    getSelectionModel().setSelectionInterval(lastGoodRow, lastGoodRow);
		                    getColumnModel().getSelectionModel().setSelectionInterval(lastGoodColumn, lastGoodColumn);
	                    }
	                }
	                else{
	                    lastGoodRow = row;
	                    lastGoodColumn = column;
	                }
                }
            }
        });
        this.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int row = getSelectedRow();
                int column = getSelectedColumn();
                if (humanEditing){
                    lastGoodRow = row;
                    lastGoodColumn = column;
                }
                else{
	                if (row == 0 || (row != -1 && column != -1 && ((String) getValueAt(row, column)).equals(""))){
	                    if (lastGoodRow != -1 && lastGoodColumn != -1){
		                    getSelectionModel().setSelectionInterval(lastGoodRow, lastGoodRow);
		                    getColumnModel().getSelectionModel().setSelectionInterval(lastGoodColumn, lastGoodColumn);
	                    }
	                }
	                else{
	                    lastGoodRow = row;
	                    lastGoodColumn = column;
	                }
                }
            }
        });
    }
    
    public void setHumanEditing(boolean theHumanEditing){
        this.humanEditing = theHumanEditing;
    }
    
    public TableCellRenderer getCellRenderer(int row,int column) {
        return new DayRenderer();
    }

    public TableCellEditor getCellEditor(int row, int column) {
        return null;
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * @return
     */
    public int getSelectedDay() {
        Object o = getValueAt(getSelectedRow(), getSelectedColumn());
        if (o == null || !(o instanceof String)){
            return -1;
        }
        try{
            return Integer.parseInt((String) o);
        }
        catch(NumberFormatException e){
            return -1;
        }
    }
}