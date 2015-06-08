package pipe.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.Highlighter;
import javax.swing.text.TableView.TableRow;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RefineryUtilities;

import pipe.dataLayer.CheckRecord;
import pipe.dataLayer.DataLayer;
import pipe.dataLayer.TCCheckRecord;
import pipe.dataLayer.TemCons;
import pipe.gui.CreateGui;
import pipe.modules.TCchecking.TCChecker.PieChartDemo2;

public class TCPanel extends JPanel {
	private ArrayList<TemCons> TCList = new ArrayList<TemCons>(6);
	JPanel Panel1 = new JPanel(new FlowLayout());
	JPanel BtnPanel = new JPanel();
	JTable table = new JTable(){
		public boolean isCellEditable(int row, int column) {
		    return false;
		   }

	};
	DefaultTableModel model = new DefaultTableModel(new String[] { "Number",
			"Activity ai", "Activity aj", "Limit Time" }, 0);
	JLabel Dr = new JLabel("Dr(");
	JTextField from = new JTextField("T", 5); // might be a Problem
	JLabel Separation = new JLabel(",");
	JTextField to = new JTextField("T", 5);
	JLabel bracket = new JLabel(") <=");
	JTextField time = new JTextField(5);
	int count = 0;
	//DataLayer dataLayer;

	public TCPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		from.setToolTipText("Please Enter Activity ai");
		to.setToolTipText("Please Enter Activity aj");
		time.setToolTipText("Please Enter Limit time");
		Panel1.add(Dr);
		Panel1.add(from);
		Panel1.add(Separation);
		Panel1.add(to);
		Panel1.add(bracket);
		Panel1.add(time);
		table.setRowHeight(20);
		
		 table.addMouseListener(new MouseAdapter() {
			   public void mouseClicked(MouseEvent e) {
			    if (e.getButton() == MouseEvent.BUTTON1) {// 单击鼠标左键
			     if (e.getClickCount() == 2) {
			     // int colummCount = table.getModel().getColumnCount();// 列数
			      int rownum = table.getSelectedRow();
			      String value = table.getModel().getValueAt(rownum, 0).toString();
			      AnswerFrame a = new AnswerFrame(value);
			      
			      
			     }
			    }
			   }
			  });
		
		//table.setRowHeight();
		/*
		 * Panel1.setBorder(new TitledBorder(new EtchedBorder
		 * (EtchedBorder.LOWERED),"Please Enter the Temporal Constraints"));
		 */
		BtnPanel.add(new ButtonBar(new String[] { "Add", "Delete", "Edit" },
				new ActionListener[] { edit, edit, edit }));
		this.add(Panel1);
		this.add(BtnPanel);
		table.setModel(model);
		this.setCenter(); // 设置单元格居中
		/**
		TableColumn column = null;
		column = table.getColumnModel().getColumn(4);
		//column.setWidth(20);
		column.setPreferredWidth(20);
		*/
	
		
		// table.getTableHeader().setForeground(Color.BLUE);
		JScrollPane scroller = new JScrollPane(table);
		scroller.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.add(scroller);

		this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),
				"Temporal Constraints"));

	}
   private ArrayList<TCCheckRecord> record = new ArrayList<TCCheckRecord>();
  
   public class AnswerFrame extends JFrame{

	public AnswerFrame(String tcNo){
		int tc = Integer.parseInt(tcNo);
		JDialog frame = new JDialog();
		//frame.setTitle("Temoporal Constraint " +tcNo);
		frame.setTitle("Detailed Information");
		//frame.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		JTextArea text = new JTextArea();
		text.setLineWrap(true);
		JLabel label = new JLabel();
	    String file = "cute.png";
	    int property = record.get(tc-1).getProperty();
	    if(property ==0){
	    	file = "r.png";
	    }
	    else if(property == 1){
	    	file = "g.png";
	    }
	    else if(property ==2 ){
	    	file = "y.png";
	    }
	    String s = record.get(tc-1).getText();
	    int [] data = new int [3];
	    data = record.get(tc-1).getData();
	    PieChartDemo demo = new PieChartDemo(data);
	    demo.setBorder(new TitledBorder(new EtchedBorder(),"Pie Chart of TC "+ tcNo));
	    
			    	    	    	  
		
		Icon p =  new ImageIcon(Thread.currentThread().getContextClassLoader().
 	              getResource(CreateGui.imgPath + file));
		label = new JLabel(p);			
		label.setOpaque(false);
		label.setSize(50, 50);
		
		JLabel tclabel = new JLabel("Checking Results of TC "+ tcNo);
		tclabel.setFont(new Font("Calibri" ,Font.BOLD,18));
		tclabel.setSize(290,50);
		JPanel headpanel = new JPanel();
		headpanel.add(label);
		headpanel.add(tclabel);
		headpanel.setBorder(new EtchedBorder());
	
		text.setText(s);
		//text.setFont(new Font("Times New Roman"))
		text.setWrapStyleWord(true);
		
		text.setSize(new Dimension(350,270));
		
		
		
		
		JScrollPane pane = new JScrollPane(text);
		pane.setSize(360 ,270);
		pane.setBorder(new TitledBorder(new EtchedBorder(),"Detailed Checking Results"+ tcNo));
		
		frame.add(pane, BorderLayout.CENTER);
		frame.add(headpanel,BorderLayout.NORTH);
		frame.add(demo ,BorderLayout.SOUTH);
		
		frame.setSize(380, 520);
		frame.setVisible(true);
		frame.setLocation(100, 100);
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	    
   }
	private ActionListener edit = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getActionCommand().equals("Add")) {
				TemCons a = new TemCons(from.getText().trim(), to.getText()
						.trim(), time.getText().trim());
				TCList.add(a);

				count++;
				String[] RowData = new String[] { Integer.toString(count),
						from.getText().trim(), to.getText().trim(),
						time.getText().trim() };
				model.addRow(RowData);
			}
			if (arg0.getActionCommand().equals("Delete")) {
				int rowIndex = table.getSelectedRow();
				count = count - 1;

				model.removeRow(rowIndex);
				TCList.remove(rowIndex);
				for (int i = rowIndex; i < table.getRowCount(); i++) {
					model.setValueAt(i + 1, i, 0);
				}

			}
			if (arg0.getActionCommand().equals("Edit")) {
				int rowIndex = table.getSelectedRow();				
				/*
				 * String fromText = (String)model.getValueAt(rowIndex, 1);
				 * from.setText(fromText);
				 * 
				 * String toText = (String)model.getValueAt(rowIndex, 2);
				 * from.setText(toText);
				 * 
				 * String timeText = (String)model.getValueAt(rowIndex, 3);
				 * from.setText(timeText);
				 */
				TemCons a = new TemCons(from.getText().trim(), to.getText()
						.trim(), time.getText().trim());
				TCList.set(rowIndex, a);
				model.setValueAt(from.getText().trim(), rowIndex, 1);
				model.setValueAt(to.getText().trim(), rowIndex, 2);
				model.setValueAt(time.getText().trim(), rowIndex, 3);

			}
			table.revalidate();
		}
	};

	public ArrayList<TemCons> getTCList() {
		return TCList;

	}
//----------------------Brilliant Work--------------------------------
	private class RowColorRenderer extends DefaultTableCellRenderer {
		int[] rows = {100,100};
        ArrayList <Integer> Rows = new ArrayList<Integer>();
        Icon p ;
        JLabel l;
		public Component getTableCellRendererComponent(JTable t, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			// 分支判断条件可根据需要进行修改
			this.setHorizontalAlignment(SwingConstants.CENTER );
			
			if(Rows.contains(row)&& column == 0){
				 /**
	    	         p =  new ImageIcon(Thread.currentThread().getContextClassLoader().
	    	              getResource(CreateGui.imgPath + "r.png"));
				l = new JLabel(p);
				
				l.setOpaque(false);
				*/
				setBackground(Color.pink);
				
				//return l;
			}
			else{
				//p = new ImageIcon(CreateGui.imgPath+"g.png");
				//l = new JLabel(p);
				//setBackground(Color.pink);
				//return l;
				//this.setIcon(p);
				setBackground(Color.white);
			}
			

			return super.getTableCellRendererComponent(t, value, isSelected,
					hasFocus, row, column);
		}
		public void setRowNum(int[] rowNum){
			rows = rowNum;
			for(int i = 0 ; i < rowNum.length; i++){
				Rows.add(rowNum[i]);
			}
		}

	}
	

	public void HighLightRows(int[] rows) {
		RowColorRenderer tcr = new RowColorRenderer();
		tcr.setRowNum(rows);
		
		for (int j = 0; j < table.getColumnCount(); j++) {
			table.getColumn(table.getColumnName(j)).setCellRenderer(tcr);

		}
		table.repaint(); // 立即变色 而不是等table添加或删除后变色
		//row = 100;// 为防止第二次搞完后变色哈

	}
	public void setCenter(){
		RowColorRenderer tcr = new RowColorRenderer();		
		for (int j = 0; j < table.getColumnCount(); j++) {
			table.getColumn(table.getColumnName(j)).setCellRenderer(tcr);

		}
		table.repaint();
	}
	//--------------------------------------------
	private class MJTable extends JTable {		
				// 实现自己的表格类
		// 重写JTable类的构造方法
		public MJTable(DefaultTableModel tableModel) {//Vector rowData, Vector columnNames
			super(tableModel);						// 调用父类的构造方法
		}
		public MJTable(){
			super();
		}
		// 重写JTable类的getTableHeader()方法
		public JTableHeader getTableHeader() {					// 定义表格头
			JTableHeader tableHeader = super.getTableHeader();	// 获得表格头对象
			tableHeader.setReorderingAllowed(false);			// 设置表格列不可重排
			DefaultTableCellRenderer hr = (DefaultTableCellRenderer) tableHeader
					.getDefaultRenderer(); 						// 获得表格头的单元格对象
			hr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);		// 设置列名居中显示
			return tableHeader;
		}
		/**
		// 重写JTable类的getDefaultRenderer(Class<?> columnClass)方法
		public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {	// 定义单元格
			DefaultTableCellRenderer cr = (DefaultTableCellRenderer) super
					.getDefaultRenderer(columnClass); 						// 获得表格的单元格对象
			cr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER); 	// 设置单元格内容居中显示
			return cr;
		}
		**/
		// 重写JTable类的isCellEditable(int row, int column)方法
		public boolean isCellEditable(int row, int column) {				// 表格不可编辑
			return false;
		}
	}
   public void setlist(ArrayList<TCCheckRecord> _checklist){
	   record = _checklist;
	  
   }
   
   public class PieChartDemo extends JPanel {

	    /**
	     * Default constructor.
	     *
	     * @param title  the frame title.
	     */
		int [] data = new int [3];
		
	    public PieChartDemo(int [] _data) {

	        //super(title);
	    	data = _data;	    		    	
	    	//this.setTitle(title);
	        final PieDataset dataset = createDataset();
	        final JFreeChart chart = createChart(dataset);
	        	        
	        // add the chart to a panel...
	        final ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setPreferredSize(new java.awt.Dimension(300, 200));
	        this.add(chartPanel);
	        //setContentPane(chartPanel);
	      
	        //this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	        	      	              	        

	    }
	   

	
	    
	    /**
	     * Creates a sample dataset.
	     * 
	     * @return a sample dataset.
	     */
	    private PieDataset createDataset() {
	        final DefaultPieDataset dataset = new DefaultPieDataset();
	        
	        //int [] a ={violatedNum,satisfiedNum,unknownNum};
	       // int [] a ={violated.size(),satisfied.size(),unknown.size()};
	        double sum = 0;
	        for(int i = 0 ; i< 3; i++){
	        	 sum += data[i];
	        }
	        double[] d = new double[3];
	        for(int i = 0; i < 3; i++){
	        	d[i] = data[i]/sum;
	        }
	        dataset.setValue("Violated", d[0]);	    
	        dataset.setValue("Unknown", d[2]);
	        dataset.setValue("Satisfied", d[1]);
	        
	        return dataset;
	    }
	    
	    /**
	     * Creates a sample chart.
	     * 
	     * @param dataset  the dataset.
	     * 
	     * @return a chart.
	     */
	    private JFreeChart createChart(final PieDataset dataset) {
	        final JFreeChart chart = ChartFactory.createPieChart(
	            "TC checking",  // chart title
	            dataset,             // dataset
	            true,                // include legend
	            true,
	            false
	        );
	        final PiePlot plot = (PiePlot) chart.getPlot();
	         //= new PieSectionLabelGenerator();
	         PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator(
	                "{0} = {2}", NumberFormat.getNumberInstance(),
	                NumberFormat.getPercentInstance());
	        plot.setLabelGenerator(generator);
	        
	        plot.setNoDataMessage("No data available");
	        plot.setExplodePercent(1, 0.30);
	        
	        return chart;
	    }
	    
	    
	}
	 

	

}
