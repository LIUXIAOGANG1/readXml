package net.sourceforge.powerswing.date;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.JSpinner.DateEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import net.sourceforge.powerswing.localization.PBundle;
import net.sourceforge.powerswing.panel.PPanel;
import net.sourceforge.powerswing.util.PComponentUtils;
import net.sourceforge.powerswing.util.date.LocalTime;
import net.sourceforge.powerswing.util.date.TimeStamp;

/**
 * 
 * 
 * @author mkerrigan2
 */
public class DatePanel extends JPanel{

    private JComboBox month;
    private JSpinner year;
    private DayTable day;
    private JSpinner time;
    private PBundle messages;
    
    public DatePanel(PBundle theMessages, LocalTime theLocalTime){
        this.messages = theMessages;
        
        this.month = new JComboBox(new String[]{messages.getString("Strings.DatePanel.January"), messages.getString("Strings.DatePanel.Februray"), messages.getString("Strings.DatePanel.March"), messages.getString("Strings.DatePanel.April"), messages.getString("Strings.DatePanel.May"), messages.getString("Strings.DatePanel.June"), messages.getString("Strings.DatePanel.July"), messages.getString("Strings.DatePanel.August"), messages.getString("Strings.DatePanel.September"), messages.getString("Strings.DatePanel.October"), messages.getString("Strings.DatePanel.November"), messages.getString("Strings.DatePanel.December")});
        Date start = new GregorianCalendar(1979, 01, 01).getTime();
        Date end = new GregorianCalendar(9999, 01, 01).getTime();
        this.year = new JSpinner(new SpinnerDateModel(start, start, end, Calendar.YEAR));
        this.year.setEditor(new DateEditor(year, "yyyy"));
        this.year.setBorder(null);
        this.year.setPreferredSize(new Dimension(60, 10));
        this.day = createTable();
        this.time = new JSpinner(new SpinnerDateModel());
        this.time.setEditor(new DateEditor(time, "HH:mm:ss"));
        this.time.setBorder(null);
        this.time.setPreferredSize(new Dimension(100, 10));
        setLocalTime(theLocalTime);
        
        PPanel monthPanel = new PPanel(1, 1, 0, 0, new Object[]{
                "", "0,1", 
                "0", month
        });
        
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timePanel.add(new PPanel(1, 1, 0, 0, new Object[]{
                "", "0", 
                "0,1", time
        }));
        
        JScrollPane jsp = new JScrollPane(new PPanel(1, 1, 0, 0, new Object[]{
                "", "0", 
                "0,1", day
        }));
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        this.addActionListeners();
        this.setLayout(new BorderLayout());
        this.add(new PPanel(3, 2, 3, 3, new Object[]{
                "", "0,1", "0", 
                "0", monthPanel, year, 
                "0,1", jsp, "<", 
                "0", timePanel, "<", 
        }));
    }
    
    /**
     * @param theLocalTime
     */
    public void setLocalTime(LocalTime theLocalTime) {
        if (theLocalTime == null){
            theLocalTime = new LocalTime();
        }
        Date d = new Date(theLocalTime.getTimeStamp(TimeZone.getDefault()).getTimeInMillis());
        this.month.setSelectedIndex(theLocalTime.getMonth() - 1);
        this.year.setValue(d);
        this.time.setValue(d);
        refresh(theLocalTime.getDayOfMonth());
    }
    
    public LocalTime getLocalTime() {
        GregorianCalendar yearGC = new GregorianCalendar();
        yearGC.setTime((Date) year.getValue());
        
        GregorianCalendar timeGC = new GregorianCalendar();
        timeGC.setTime((Date) time.getValue());
        
        int dayNum = day.getSelectedDay();
        int monthNum = month.getSelectedIndex() + 1;
        int yearNum = yearGC.get(GregorianCalendar.YEAR);
        int hour = timeGC.get(GregorianCalendar.HOUR_OF_DAY);
        int minute = timeGC.get(GregorianCalendar.MINUTE);
        int second = timeGC.get(GregorianCalendar.SECOND);

        return new LocalTime(yearNum, monthNum, dayNum, hour, minute, second);
    }

    /**
     * 
     */
    private void addActionListeners() {
        month.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                refresh();
            }
        });
        
        year.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                refresh();
            }
        });
    }

    /**
     * @return
     */
    private DayTable createTable() {
        DayTable jt = new DayTable();
        PComponentUtils.fixTabbing(jt);
        jt.setVisible(true);
        jt.setPreferredScrollableViewportSize(new Dimension(175, 140));
        jt.setRowHeight(18);
        jt.setColumnSelectionAllowed(false);
        jt.setGridColor(Color.white);
        
        JTableHeader header = jt.getTableHeader();
        header.setReorderingAllowed(false);
        header.setVisible(false);
        header.setResizingAllowed(false);
        return jt;
    }

    /**
     * @return
     */
    private void refresh() {
        refresh(Integer.parseInt((String) day.getValueAt(day.getSelectedRow(), day.getSelectedColumn())));
    }
    
    private void refresh(int theDayNum) {
        int monthNum = month.getSelectedIndex() + 1;
        int yearNum = new TimeStamp(((Date) year.getValue()).getTime()).getLocalTime(TimeZone.getDefault()).getYear();
        int nextMonthNum = monthNum + 1;
        int nextYearNum = yearNum;
        if (nextMonthNum == 13){
            nextMonthNum = 1;
            nextYearNum++;
        }
        
        LocalTime thisMonth = new LocalTime(yearNum, monthNum, 1);
        LocalTime nextMonth = new LocalTime(nextYearNum, nextMonthNum, 1);
        
        int dayNum = thisMonth.getDayOfWeek();
        if (dayNum == 7){
            dayNum = 0; //Makes sunday be 0 ratrher than 7
        }
        int numDays = nextMonth.plusDays(-1).getDayOfMonth();
        
        int[] sel = new int[]{-1, -1};
        Object[][] res = new Object[7][];
        res[0] = new Object[]{messages.getString("Strings.DatePanel.Sunday"), messages.getString("Strings.DatePanel.Monday"), messages.getString("Strings.DatePanel.Tuesday"), messages.getString("Strings.DatePanel.Wednesday"), messages.getString("Strings.DatePanel.Thursday"), messages.getString("Strings.DatePanel.Friday"), messages.getString("Strings.DatePanel.Saturday")};
        int k = -dayNum;
        for (int i = 1; i < 7; i++) {
            Object[] row = new Object[7];
            for (int j = 0; j < 7; j++) {
                if (k >= 0 && k < numDays){
                    row[j] = "" + (k+1);
                    if (theDayNum == (k+1)){
                        sel = new int[]{i, j};
                    }
                }
                else{
                    row[j] = "";
                }
                k++;
            }
            res[i] = row;
        }
        
        ((DefaultTableModel) day.getModel()).setDataVector(res, new String[]{"", "", "", "", "", "", ""});
        
        for(int i = 0; i < day.getColumnCount(); i++){
            TableColumn column = day.getColumnModel().getColumn(i);
            column.setPreferredWidth(25);
            column.setWidth(25);
            column.setMinWidth(25);
            column.setMaxWidth(25);
        }
        
        day.setHumanEditing(true);
        if (sel[0] != -1 && sel[1] != -1){
            day.getSelectionModel().setSelectionInterval(sel[0], sel[0]);
            day.getColumnModel().getSelectionModel().setSelectionInterval(sel[1], sel[1]);
        }
        day.setHumanEditing(false);
        
        day.invalidate();
        day.repaint();
        day.validate();
    }
    
    public void setKeyListeners(KeyListener EnterKeyListener, KeyListener EscKeyListener, MouseAdapter theDoubleClickListener){
        month.addKeyListener(EscKeyListener);
        ((JSpinner.DefaultEditor) year.getEditor()).getTextField().addKeyListener(EnterKeyListener);
        ((JSpinner.DefaultEditor) year.getEditor()).getTextField().addKeyListener(EscKeyListener);
        day.addKeyListener(EnterKeyListener);
        day.addKeyListener(EscKeyListener);
        day.addMouseListener(theDoubleClickListener);
        ((JSpinner.DefaultEditor) time.getEditor()).getTextField().addKeyListener(EnterKeyListener);
        ((JSpinner.DefaultEditor) time.getEditor()).getTextField().addKeyListener(EscKeyListener);
    }
}