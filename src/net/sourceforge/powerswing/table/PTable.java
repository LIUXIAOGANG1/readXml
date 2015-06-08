package net.sourceforge.powerswing.table;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class PTable extends JTable{

    private boolean canEditCells = false;
    
    public PTable() {
        super();
    }

    public PTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }
    
    public PTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }
    
    public PTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
    }

    public PTable(TableModel dm) {
        super(dm);
    }

    public PTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public PTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }
    
    public boolean canEditCells() {
        return canEditCells;
    }
    
    public void setCanEditCells(boolean canEditCells) {
        this.canEditCells = canEditCells;
    }
    
    public boolean isCellEditable(int row, int column) {
        return canEditCells && super.isCellEditable(row, column);
    }
    
    // Useful Utility Methods
    public void increaseColumnWidthByFactor(int theColumnNumber, int theFactor){
        TableColumn col = getColumnModel().getColumn(theColumnNumber);
        col.setPreferredWidth(col.getWidth() * theFactor);
    }
    
    public void reduceColumnWidthByFactor(int theColumnNumber, int theFactor){
        TableColumn col = getColumnModel().getColumn(theColumnNumber);
        col.setPreferredWidth(col.getWidth() / theFactor);
    }
}
