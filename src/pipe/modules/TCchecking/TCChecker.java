package pipe.modules.TCchecking;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Checkbox;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.exolab.castor.util.Iterator;
import org.jfree.ui.RefineryUtilities;

import jpowergraph.PIPEInitialState;
import jpowergraph.PIPEInitialTangibleState;
import jpowergraph.PIPEInitialVanishingState;
import jpowergraph.PIPELoopWithTextEdge;
import jpowergraph.PIPEState;
import jpowergraph.PIPEVanishingState;
import jpowergraph.PIPETangibleState;

// BingXue
import jpowergraph.SGNode;
import pipe.gui.widgets.SGraphFrame;

import net.sourceforge.jpowergraph.defaults.DefaultGraph;
import net.sourceforge.jpowergraph.defaults.DefaultNode;
import net.sourceforge.jpowergraph.defaults.TextEdge;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import pipe.dataLayer.CheckRecord;
import pipe.dataLayer.DataLayer;
import pipe.dataLayer.Path;
import pipe.dataLayer.Place;
import pipe.dataLayer.SPArc;
import pipe.dataLayer.SPNode;
import pipe.dataLayer.SatisfiedRecord;
import pipe.dataLayer.TCCheckRecord;
import pipe.dataLayer.TemCons;
import pipe.dataLayer.TempCont;
import pipe.dataLayer.UnknownRecord;
import pipe.dataLayer.calculations.StateSpaceGenerator;
import pipe.dataLayer.calculations.TimelessTrapException;
import pipe.dataLayer.calculations.myTree;
import pipe.gui.CreateGui;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.GraphFrame;
import pipe.gui.widgets.LPanel;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.PieChartDemo7;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.gui.widgets.TCPanel;
import pipe.io.AbortDotFileGenerationException;
import pipe.io.ImmediateAbortException;
import pipe.io.IncorrectFileFormatException;
import pipe.io.RGFileHeader;
import pipe.io.StateRecord;
import pipe.io.TransitionRecord;
import pipe.modules.Module;

public class TCChecker implements Module {

	private static final String MODULE_NAME = "Constraint Checking";
	private PetriNetChooserPanel sourceFilePanel;
	private static TCPanel tcpanel;
	private static ResultsHTMLPane results;
	private static LPanel lpanel;
	private EscapableDialog guiDialog = new EscapableDialog(CreateGui.getApp(),
			MODULE_NAME, true);
	  private static Checkbox checkBox1 =  
          new Checkbox("Show the Pie Chart of Checking Results", false);
	private static String dataLayerName;

	private static DataLayer pnmlData;

	private static HashMap<Integer, Integer> placeIndex;

	private static ArrayList<SPNode> nodelist = new ArrayList<SPNode>();
	private static ArrayList<SPArc> arclist = new ArrayList<SPArc>();
	// private static ArrayList <TempCont> TCList = new ArrayList<TempCont>();
	private static ArrayList<TemCons> tclist = new ArrayList<TemCons>();
	private static ArrayList<CheckRecord> checklist = new ArrayList<CheckRecord>();
	
	private static int satisfiedNum;
	private static int unknownNum;
	private static int violatedNum;
	
	private static ArrayList<Integer> satisfied = new ArrayList<Integer>();
	private static ArrayList<Integer> unknown = new ArrayList<Integer>();
	private static ArrayList<Integer> violated = new ArrayList<Integer>();

	public void run(DataLayer pnmlData) {
		// Build interface

		// 1 Set layout
		Container contentPane = guiDialog.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

		// 2 Add file browser
		sourceFilePanel = new PetriNetChooserPanel("Source net", pnmlData);
		contentPane.add(sourceFilePanel);

		tcpanel = new TCPanel();
		contentPane.add(tcpanel);

		// 3 Add results pane
		
		results = new ResultsHTMLPane(pnmlData.getURI());
		contentPane.add(results);
		contentPane.add(checkBox1);

		// 4 Add button's
		contentPane.add(new ButtonBar("Temporal Constraint Checking", tcCheck,
				guiDialog.getRootPane()));
		/**
		contentPane.add(new ButtonBar("Pie Chart", generateChart,
				guiDialog.getRootPane()));
*/
		
		//contentPane.add(results);
		 //lpanel = new LPanel();
		//contentPane.add(lpanel);

		// 5 Make window fit contents' preferred size
		guiDialog.pack();
		checkBox1.setState(true);
		// 6 Move window to the middle of the screen
		guiDialog.setLocationRelativeTo(null);
		
		guiDialog.setSize(500, 600); // add to control the size by Bingxue
		// checkBox1.setState(false);
		guiDialog.setModal(false);
		
		guiDialog.setVisible(false);
		guiDialog.setVisible(true);

	}
	/*
	private ActionListener generateChart = new ActionListener(){
		public void actionPerformed(ActionEvent arg0){
			generateGraph();
		}

	};
	*/
	private ActionListener tcCheck = new ActionListener() {

		public void actionPerformed(ActionEvent arg0) {

			DataLayer sourceDataLayer = sourceFilePanel.getDataLayer();
			dataLayerName = sourceDataLayer.pnmlName;
			String s = "";

			s += "<h2>Constraint Checking Graph Results</h2>";
			// int h = s.length();

			if (sourceDataLayer == null) {
				JOptionPane.showMessageDialog(null,
						"Please, choose a source net", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!sourceDataLayer.hasPlaceTransitionObjects()) {
				s += "No Petri net objects defined!";
			} else {
				try {

					// String graph = "Sprouting graph";

					System.gc();
					mapInitialize(sourceDataLayer);
					
					tclist = tcpanel.getTCList();
					
					checklist.clear();
					check(sourceDataLayer);
					
					tcpanel.setlist(record);
					if(checklist.size()== 0){
						s +="No temporal constraints are violated.";
					}
					else{
						s += generateChecktable();
					}
					if(checkBox1.getState()){
						getProperties();
						generateGraph();
					}
					//generateGraph();
					
				
					results.setEnabled(true);
					
					
				} catch (OutOfMemoryError e) {
					s += "Memory error: " + e.getMessage();
					results.setText(s);
					return;
				} catch (StackOverflowError e) {
					s += "StackOverflow Error";
					results.setText(s);
					return;
				}
				/**
				 * catch (ImmediateAbortException e) { s += "<br>
				 * Error: " + e.getMessage(); results.setText(s); return; }
				 * catch (TimelessTrapException e) { s += "<br>
				 * " + e.getMessage(); results.setText(s); return; } catch
				 * (IOException e) { s += "<br>
				 * " + e.getMessage(); results.setText(s); return; }
				 */
				catch (Exception e) {
					e.printStackTrace();
					s += "<br>Error";
					results.setText(s);
					return;
				} finally {
					
				}
			}
			//lpanel.setThings();
			results.setText(s);
			//lpanel.setThings(tclist);
			//results.add(lpanel);
		}
	};
	public void generateGraph(){
		 PieChartDemo2 demo = new PieChartDemo2("Pie Chart of Checking results");
	        demo.pack();
	        demo.setIconImage((
  	              new ImageIcon(Thread.currentThread().getContextClassLoader().
  	              getResource(CreateGui.imgPath + "cute.png")).getImage()));
	        //demo.setIconImage( new ImageIcon(CreateGui.imgPath+"cute.png").getImage());
	        RefineryUtilities.centerFrameOnScreen(demo);
	        demo.setVisible(true);
	       // demo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	       

	}
	public String getName() {
		return MODULE_NAME;
	}

	// Initialize the lists and get map initialize
	private static void mapInitialize(DataLayer dataLayer) {
		placeIndex = new HashMap<Integer, Integer>();
		nodelist.clear();
		arclist.clear();
		dataLayer.getSPListsData(nodelist, arclist);
		//dataLayer.getSPLists(nodelist, arclist);
		for (int i = 0; i < nodelist.size(); i++) {
			placeIndex.put(nodelist.get(i).getPlaceNo(), i);
		}

	}
/*
	public static String generateNodetable(DataLayer dataLayer) {
		ArrayList res = new ArrayList();
		String label; // may need intialize
		String placeNo;
		String timeset;
		String pathset;
		if (nodelist.size() == 0) {
			mapInitialize(dataLayer);
		} else if (res.size() < arclist.size()) {
			res.add("Nodes");
			res.add("Corresponding to");
			res.add("Path sets");
			res.add("Time sets");

			for (int i = 0; i < nodelist.size(); i++) {
				SPNode N = nodelist.get(i).deepCopy();
				// N = nodelist.get(i);
				// label = String.valueOf(i);
				label = "Node" + i;
				placeNo = N.getPlaceNoString();
				pathset = N.getPathsetString();
				timeset = N.getTimesetString();
				res.add(label);
				res.add(placeNo);
				res.add(pathset);
				res.add(timeset);
			}

		}
		return ResultsHTMLPane.makeTable(res.toArray(), 4, false, true, true,
				true);
	}
	*/
/**
	public static String generateArctable(DataLayer dataLayer) {
		if (arclist.size() == 0) {
			// arclist.clear();
			mapInitialize(dataLayer);

		}

		ArrayList<String> res = new ArrayList<String>();
		res.add("Arc");
		res.add("From Place");
		res.add("To Place");
		res.add("Arc Info");
		String arclabel;
		String arcfrom;
		String arcto;
		String text;

		for (int i = 0; i < arclist.size(); i++) {

			SPArc A = new SPArc();
			A = arclist.get(i);
			arclabel = "Arc" + i;
			arcfrom = dataLayer.getPlace(A.getFrom()).getName();
			arcto = dataLayer.getPlace(A.getTo()).getName();
			int transition = A.getTransNo();
			text = dataLayer.getArcText(transition);
			res.add(arclabel);
			res.add(arcfrom);
			res.add(arcto);
			res.add(text);
		}
		return ResultsHTMLPane.makeTable(res.toArray(), 4, false, true, true,
				true);
	}
*/
	public static int[] getFromByTransNo(int transition) {
		int count = 0;
		int[] fromPlaces = null;
		ArrayList <Integer> from = new ArrayList<Integer>();
		for (int i = 0; i < arclist.size(); i++) {
			if (arclist.get(i).getTransNo() == transition &&
					!from.contains(arclist.get(i).getFrom())) {
				//count++;
				from.add(arclist.get(i).getFrom());
			}
		}
		//fromPlaces = new int[count];
		fromPlaces = new int[from.size()];
		for(int i = 0; i < from.size() ; i++){
			fromPlaces[i]= from.get(i).intValue();
		}
		/*
		count = 0;
		for (int i = 0; i < arclist.size(); i++) {
			if (arclist.get(i).getTransNo() == transition) {
				fromPlaces[count++] = arclist.get(i).getFrom();
			}
		}
		*/
		return fromPlaces;

	}

	public static int[] getToByTransNo(int transition) {
		int count = 0;
		int[] toPlaces = null;
		ArrayList <Integer> to = new ArrayList<Integer>();
		for (int i = 0; i < arclist.size(); i++) {
			if (arclist.get(i).getTransNo() == transition
					&& !to.contains(arclist.get(i).getTo())) {
				//count++;
				to.add(arclist.get(i).getTo());
				// arclist.get(i).getFrom();
			}
		}
		toPlaces = new int[to.size()];
		for(int i = 0; i < to.size(); i++){
			toPlaces[i] = to.get(i).intValue();
		}
		/**
		//count = 0;
		for (int i = 0; i < arclist.size(); i++) {
			if (arclist.get(i).getTransNo() == transition) {
				toPlaces[count++] = arclist.get(i).getTo();
			}
		}
		*/
		return toPlaces;

	}

	// remember to initialize the tclist
	private static ArrayList<TCCheckRecord> record = new ArrayList<TCCheckRecord>();
	public static void check(DataLayer dataLayer) {

		checklist.clear();
		record.clear();
	
		
		boolean sameProcess = false;

		for (int i = 0; i < tclist.size(); i++) {
			
			 ArrayList<CheckRecord> violatedlist  = new ArrayList<CheckRecord>();
			 ArrayList<SatisfiedRecord> satisfiedlist = new ArrayList<SatisfiedRecord>();
			 ArrayList<UnknownRecord>  unknownlist = new  ArrayList<UnknownRecord>();
			 
			TemCons tc = new TemCons(tclist.get(i));
			int from = dataLayer.getTransitionNoByName(tc.getFrom());
			int to = dataLayer.getTransitionNoByName(tc.getTo());
			// 测试是否在同一个过程中
			sameProcess = dataLayer.SameProcess(from, to);
			int[] transitions;
			// get the maxium waiting time
			int waitingtime = 0;
			String solution;

			int time = Integer.parseInt(tc.getTime());

			int[] fromPlaces = getFromByTransNo(from);
			int[] toPlaces = getToByTransNo(to);
			if (fromPlaces.length == 1 && toPlaces.length == 1) {
				int fromNodeIndex = placeIndex.get(fromPlaces[0]);
				int toNodeIndex = placeIndex.get(toPlaces[0]);

				SPNode fromNode = nodelist.get(fromNodeIndex).deepCopy();
				SPNode toNode = nodelist.get(toNodeIndex).deepCopy();

				for (int j = 0; j < fromNode.getTimeset().Timeset.size(); j++) {
					int late1 = fromNode.getTimeset().Timeset.get(j)
							.getEndtime();
					int early1 =  fromNode.getTimeset().Timeset.get(j)
					.getStarttime();
					for (int k = 0; k < toNode.getTimeset().Timeset.size(); k++) {
						int early2 = toNode.getTimeset().Timeset.get(k)
								.getStarttime();
						int late2 =  toNode.getTimeset().Timeset.get(k)
						.getEndtime();
						if (early2 - late1 > time) {
							
							if(sameProcess){
								solution = getSameProcessSolution(fromNode.getPathset()
										.pathSet.get(j),
										toNode.getPathset().pathSet.get(k));
								waitingtime = getSameWaitingTime(fromNode.getPathset()
										.pathSet.get(j),
										toNode.getPathset().pathSet.get(k),dataLayer);
										
								
							}
							else{
								solution = getSolutions(toNode.getPathset().pathSet.get(k));
								waitingtime = getDiffWaitingTime(toNode.getPathset().pathSet.get(k),
										dataLayer);
							}
							int reduced = early2 - late1 - time;							
							int dur = reduced + waitingtime;
							solution += dur;
							
							violatedNum++;
							CheckRecord a = new CheckRecord(i, dur,
									fromNode.getPathset().pathSet.get(j),
									toNode.getPathset().pathSet.get(k),
									sameProcess,solution);
							checklist.add(a);
							
							violatedlist.add(a);

						}
						else if(late2 - early1 <= time){
							satisfiedNum++;
							SatisfiedRecord s = new SatisfiedRecord(
									fromNode.getPathset().pathSet.get(j),
									toNode.getPathset().pathSet.get(k),
									sameProcess);
							satisfiedlist.add(s);
							
							
						}
						else{
							UnknownRecord u = new UnknownRecord(
									fromNode.getPathset().pathSet.get(j),
									toNode.getPathset().pathSet.get(k),
									sameProcess);
							unknownlist.add(u);
							
						     unknownNum++;
						}

					}
				}
				
				TCCheckRecord r = new TCCheckRecord(i,sameProcess,violatedlist,satisfiedlist,
						unknownlist);
				record.add(r);
				

			} else {
				for (int m = 0; m < fromPlaces.length; m++) {
					int fromNodeIndex = placeIndex.get(fromPlaces[m]);
					for (int n = 0; n < toPlaces.length; n++) {
						int toNodeIndex = placeIndex.get(toPlaces[n]);
						SPNode fromNode = nodelist.get(fromNodeIndex)
								.deepCopy();
						SPNode toNode = nodelist.get(toNodeIndex).deepCopy();

						for (int j = 0; j < fromNode.getTimeset().Timeset
								.size(); j++) {
							int early1 = fromNode.getTimeset().Timeset.get(j)
									.getStarttime();
							int late1 = fromNode.getTimeset().Timeset.get(j)
							.getEndtime();
							for (int k = 0; k < toNode.getTimeset().Timeset
									.size(); k++) {
								int early2 = toNode.getTimeset().Timeset.get(k)
								.getStarttime();
								int late2 = toNode.getTimeset().Timeset.get(k)
										.getEndtime();
								if (early2- late1 > time) {
									
									if(sameProcess){
										solution = getSameProcessSolution(fromNode.getPathset().pathSet.get(j),
												toNode.getPathset().pathSet.get(k));
										waitingtime = getSameWaitingTime(fromNode.getPathset()
												.pathSet.get(j),
												toNode.getPathset().pathSet.get(k),dataLayer);
									}
									else{
										solution = getSolutions(toNode.getPathset().pathSet.get(k));
										waitingtime = getDiffWaitingTime(toNode.getPathset().pathSet.get(k),
												dataLayer);
									}
									int reduced = early2- late1 - time;
									int dur = reduced + waitingtime;
									solution += dur;
									
									violatedNum++;
									CheckRecord a = new CheckRecord(i, dur,
											fromNode.getPathset().pathSet
													.get(j),
											toNode.getPathset().pathSet.get(k),
											sameProcess,solution);
									checklist.add(a);
									violatedlist.add(a);
									

								}
								else if(late2 - early1 <= time){
									satisfiedNum++;
									SatisfiedRecord s = new SatisfiedRecord(
											fromNode.getPathset().pathSet.get(j),
											toNode.getPathset().pathSet.get(k),
											sameProcess);
									satisfiedlist.add(s);
									
								}
								else{
									
								     unknownNum++;
								     UnknownRecord u = new UnknownRecord(
												fromNode.getPathset().pathSet.get(j),
												toNode.getPathset().pathSet.get(k),
												sameProcess);
										unknownlist.add(u);
								     
								}


							}
						}
					}

				}
				TCCheckRecord r = new TCCheckRecord(i,sameProcess,violatedlist,satisfiedlist,
						unknownlist);
				record.add(r);
			}
			

		}		
		highLight();
		
	}

	public static String getSolutions1(int[] transitions, DataLayer dataLayer) {
		StringBuilder a = new StringBuilder(
				dataLayer.getTransitionName(transitions[0]));
		for (int i = 1; i < transitions.length; i++) {
			a.append("+").append(dataLayer.getTransitionName(transitions[i]));
		}
		a.append("=");
		return a.toString();

	}
	public static String getSolutions(Path ph){
		ArrayList<String> transitions = ph.getTransitions();
		StringBuilder a = new StringBuilder(transitions.get(0));
		for(int i = 1 ; i < transitions.size(); i++){
			a.append("+").append(transitions.get(i));
			
		}
		a.append("=");
		return a.toString();
	}
	public static String getSameProcessSolution(Path a , Path b){
		ArrayList <String> pha = new ArrayList<String>();
		ArrayList <String> phb = new ArrayList<String>();
		pha.addAll(a.getTransitions());
		phb.addAll(b.getTransitions());
		phb.removeAll(pha);
		StringBuilder s = new StringBuilder(phb.get(0));
		for(int i = 1 ; i < phb.size() ; i++){
			s.append("+").append(phb.get(i));			
		}
		s.append("=");
		return s.toString();
		
	}
	//------------------------------------------------------------------
	public static int getSameWaitingTime(Path a, Path b , DataLayer dataLayer){
		int res;
		ArrayList <String> pha = new ArrayList<String>();
		ArrayList <String> phb = new ArrayList<String>();
		pha.addAll(a.getTransitions());
		phb.addAll(b.getTransitions());
		phb.removeAll(pha);
		int [] transitions = new int [2]; // just initialize no meaning
			transitions = toArray(phb,dataLayer);
			res = dataLayer.waitingTime(transitions);
			return res;
	}
	public static int getDiffWaitingTime(Path b , DataLayer dataLayer){
		int res;
		ArrayList <String> phb = new ArrayList<String>();
		phb.addAll(b.getTransitions());
		int [] transitions = new int [2]; // just initialize no meaning
		transitions = toArray(phb,dataLayer);
		res = dataLayer.waitingTime(transitions);
		return res;
	}
	// 实现了从变迁名到变迁序号的转换
	public static int [] toArray(ArrayList <String> list, DataLayer dataLayer){
		int [] array = new int[list.size()];
		for(int i = 0 ; i < list.size(); i ++){
			int transition = dataLayer.getTransitionNoByName(list.get(i));
			array[i] = transition;
		}
		return array;
	}
//------------------------------------------------------------------
	// ---------------make the check table--------------
	public static String generateChecktable() {
		ArrayList res = new ArrayList();
		String label; // may need intialize
		String reduced;
		
		String patha;
		String pathb;
		String ErrorPath;
		
		String solution;
		if (nodelist.size() == 0) {
			// mapInitialize(dataLayer);
		}
		else {
			res.add("Violated No");
			res.add("TimeToReduce");
			res.add("Error Path");
			res.add("Solution");

			for (int i = 0; i < checklist.size(); i++) {
				CheckRecord N = new CheckRecord();
				N = checklist.get(i);
				// N = nodelist.get(i);
				// label = String.valueOf(i);
				label = Integer.toString(Integer.parseInt(N.gettcNo())+ 1);
				reduced = N.getReduced();
				
				patha = N.getPath1();
				pathb = N.getPath2();
				
				solution = N.getSolution();
				
				
				if(N.sameProcess()){
					ErrorPath = pathb;
				}
				else{
					ErrorPath = patha + pathb;
				}
				res.add(label);
				res.add(reduced);
				res.add(ErrorPath);
				res.add(solution);
			}

		}
		return ResultsHTMLPane.makeTable(res.toArray(), 4, false, true, true,
				true);
	}
	public static void highLight(){
		int [] rows = {100,100};
		if(checklist.size()== 0){
			tcpanel.HighLightRows(rows);
			// there's no data in checklist indicates that there's no row to be highlight 
			// so set the row number to be 100
		}
		ArrayList <String> Rows = new ArrayList<String>(checklist.size());
		for(int i = 0; i < checklist.size(); i++){
			Rows.add(checklist.get(i).gettcNo());
			//int row = Integer.parseInt(checklist.get(i).gettcNo());
		  //  tcpanel.HighLightRows(row);
		}
		rows = new int[Rows.size()];
		for(int i = 0 ; i <Rows.size(); i++){
			rows[i] = Integer.parseInt(Rows.get(i));
		}
		tcpanel.HighLightRows(rows);
	}
	
	
	
	public class PieChartDemo2 extends JDialog {

	    /**
	     * Default constructor.
	     *
	     * @param title  the frame title.
	     */
		
		
	    public PieChartDemo2(final String title) {

	        //super(title);
	    	
	    	
	    	//JPanel panel = new JPanel(new GridLayout(1, 2));
	    	this.setTitle(title);
	        final PieDataset dataset = createDataset();
	        final JFreeChart chart = createChart(dataset);
	        	        
	        // add the chart to a panel...
	        final ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setPreferredSize(new java.awt.Dimension(400, 270));
	        setContentPane(chartPanel);
	      
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
	        int [] a ={violated.size(),satisfied.size(),unknown.size()};
	        double sum = 0;
	        /**
	        for(int i = 0 ; i< 3; i++){
	        	 sum += a[i];
	        }
	        **/
	        sum =record.size();
	        double[] d = new double[3];
	        for(int i = 0; i < 3; i++){
	        	d[i] = a[i]/sum;
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
	
	public void getProperties(){
		for(int i = 0 ; i < record.size();i++){
			if(record.get(i).getProperty()==0){
				violated.add(record.get(i).gettcNo());
			}
			else if(record.get(i).getProperty()==1){
				satisfied.add(record.get(i).gettcNo());
			}
			else if(record.get(i).getProperty()==2){
				unknown.add(record.get(i).gettcNo());
			}
			else{
			System.out.print("Property Problem");
			}
		}
		
	}

}
