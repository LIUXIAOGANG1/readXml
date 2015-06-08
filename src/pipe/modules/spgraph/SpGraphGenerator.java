package pipe.modules.spgraph;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Checkbox;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.exolab.castor.util.Iterator;

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

import pipe.dataLayer.DataLayer;
import pipe.dataLayer.Place;
import pipe.dataLayer.SPArc;
import pipe.dataLayer.SPNode;
import pipe.dataLayer.calculations.StateSpaceGenerator;
import pipe.dataLayer.calculations.TimelessTrapException;
import pipe.dataLayer.calculations.myTree;
import pipe.gui.CreateGui;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.GraphFrame;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.io.AbortDotFileGenerationException;
import pipe.io.ImmediateAbortException;
import pipe.io.IncorrectFileFormatException;
import pipe.io.RGFileHeader;
import pipe.io.StateRecord;
import pipe.io.TransitionRecord;
import pipe.modules.Module;


/**
@author Bingxue

 */
public class SpGraphGenerator 
        implements Module {
	
   private static final String MODULE_NAME = "Sprouting Graph";
   private PetriNetChooserPanel sourceFilePanel;
   private static ResultsHTMLPane results;
   private EscapableDialog guiDialog = 
           new EscapableDialog(CreateGui.getApp(), MODULE_NAME, true);
   public static TransitionRecord newtrans = new TransitionRecord();
 
   // 注意看还需不需要用这个checkbox 或者改成别的什么
   /*
   private static Checkbox checkBox1 =  
           new Checkbox("Show the intial state(S0) in a different color", false);
   */
   private static String dataLayerName;
   
   private static DataLayer pnmlData;
   
   private static HashMap <Integer , Integer>  placeIndex;
   
   private static ArrayList <SPNode> nodelist = new ArrayList <SPNode>();
   private static ArrayList <SPArc> arclist = new ArrayList <SPArc>();
   
   public void run(DataLayer pnmlData) {
      // Build interface
	 // pnmlData.test();
      
      // 1 Set layout
      Container contentPane = guiDialog.getContentPane();
      contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

      // 2 Add file browser
      sourceFilePanel = new PetriNetChooserPanel("Source net", pnmlData);
      contentPane.add(sourceFilePanel);

      // 3 Add results pane
      results = new ResultsHTMLPane(pnmlData.getURI());
      contentPane.add(results);

      // 4 Add button's
      contentPane.add(new ButtonBar("Generate Sprouting Graph", generateGraph,
              guiDialog.getRootPane()));
      //contentPane.add(new ButtonBar("Generate Reachability Tree", generateGraph,
              //guiDialog.getRootPane()));
 //     contentPane.add(checkBox1);      

      // 5 Make window fit contents' preferred size
      guiDialog.pack();

      // 6 Move window to the middle of the screen
      guiDialog.setLocationRelativeTo(null);
      
      //checkBox1.setState(false);
      guiDialog.setModal(false);
      //guiDialog.setVisible(false);
      guiDialog.setVisible(true);
      
      
   }

   
   private ActionListener generateGraph = new ActionListener() {
       
      public void actionPerformed(ActionEvent arg0) {
    	  /*
         long start = new Date().getTime();
         long gfinished;
         long allfinished;
         double graphtime;
         double constructiontime;
         double totaltime;
         */
         DataLayer sourceDataLayer = sourceFilePanel.getDataLayer();
         dataLayerName = sourceDataLayer.pnmlName;
         
         // This will be used to store the reachability graph data
         //File reachabilityGraph = new File("results.rg");
         
         // This will be used to store the steady state distribution
         String s = "<h2>Sprouting Graph Results</h2>";
         
         if (sourceDataLayer == null){
            JOptionPane.showMessageDialog( null, "Please, choose a source net", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
         }
         
         if (!sourceDataLayer.hasPlaceTransitionObjects()) {
            s += "No Petri net objects defined!";
         } else {
            try {
               
               String graph = "Sprouting graph";
                                          
               System.gc();
               generateGraph(sourceDataLayer);
               s += ResultsHTMLPane.makeTable(new String[]{
            		   "Nodes of Sprouting Graph ",
            		   generateNodetable(sourceDataLayer)
               }, 1, false, false, true, false);
               
               //generateGraph(sourceDataLayer);
               s += ResultsHTMLPane.makeTable(new String[]{
            		   "Arcs of Sprouting Graph ",
            		   generateArctable(sourceDataLayer)
               }, 1, false, false, true, false);
               
              
               
               
               /*       
               allfinished = new Date().getTime();
               graphtime = (gfinished - start)/1000.0;
               constructiontime = (allfinished - gfinished)/1000.0;
               totaltime = (allfinished - start)/1000.0;
               DecimalFormat f=new DecimalFormat();
               f.setMaximumFractionDigits(5);
               s += "<br>Generating " + graph + " took " + 
                       f.format(graphtime) + "s";
               s += "<br>Converting to dot format and constructing took " + 
                       f.format(constructiontime) + "s";
               s += "<br>Total time was " + f.format(totaltime) + "s";	
               */
               results.setEnabled(true);
            } catch (OutOfMemoryError e) {
               s += "Memory error: " + e.getMessage();
               results.setText(s);
               return;
            } catch (StackOverflowError e) {
               s += "StackOverflow Error";
               results.setText(s);
               return;               
            } catch (ImmediateAbortException e) {
               s += "<br>Error: " + e.getMessage();
               results.setText(s);
               return;
            } catch (TimelessTrapException e) {
               s += "<br>" + e.getMessage();
               results.setText(s);
               return;
            } catch (IOException e) {
               s += "<br>" + e.getMessage();
               results.setText(s);
               return;
            } catch (Exception e) {
               e.printStackTrace();
               s += "<br>Error";
               results.setText(s);
               return;
            } finally {
            	/*
               if (reachabilityGraph.exists()) {
                  reachabilityGraph.delete();
               }
               */
            }
         }
         results.setText(s);
      }
   };

   
   public String getName() {
      return MODULE_NAME;
   }

   
   private void generateGraph(DataLayer dataLayer)
            throws AbortDotFileGenerationException, 
           IOException, Exception{
      DefaultGraph graph = createGraph(dataLayer);
      SGraphFrame frame = new SGraphFrame();
      ArrayList <String> sequence = new ArrayList<String>();
      sequence = dataLayer.getseqString();
      String legend ="";
      if(sequence.size() >0){
    	  legend = "{" + sequence.get(0);
      }
      for(int i = 1 ; i < sequence.size(); i++){
    	  legend += "," +sequence.get(i);
      }
      legend +="}";
      
      
      frame.constructGraphFrame(graph, legend);
      frame.toFront();
      frame.setIconImage((
              new ImageIcon(Thread.currentThread().getContextClassLoader().
              getResource(CreateGui.imgPath + "cute.png")).getImage()));
      frame.setTitle(dataLayerName);
   }
   
   // Initialize the lists and get map initialize
   private static void mapInitialize(DataLayer dataLayer){
	   placeIndex = new HashMap<Integer ,Integer>();
	   nodelist.clear();// 这行语句保证了在打开两次文件并单击生成图按钮时不会在
	                     //已经计算过的nodelist再次添加 从而使生成图出现问题
	   arclist.clear();
	   dataLayer.getSPLists(nodelist, arclist);
	   for(int i = 0 ; i < nodelist.size() ; i++){
		   placeIndex.put(nodelist.get(i).getPlaceNo(), i);
	   }
	   
   }
 
      
   private static DefaultGraph createGraph(DataLayer dataLayer
             ) throws IOException{
      DefaultGraph graph = new DefaultGraph();
      
      
      mapInitialize(dataLayer);
      
      
      ArrayList nodes = new ArrayList();
      ArrayList edges = new ArrayList();
      ArrayList loopEdges = new ArrayList();
      ArrayList loopEdgesTransitions = new ArrayList();
      String label ; // may need intialize
      String placeNo;
      String timeset;
      String pathset;
      
      
      for(int i = 0 ; i < nodelist.size() ; i++){
    	  SPNode  N = nodelist.get(i).deepCopy();
    	  // N =  nodelist.get(i);
    	  label = String.valueOf(i);
    	  placeNo = dataLayer.getPlace(N.getPlaceNo()).getName();
    	  //placeNo = N.getPlaceNoString();
    	  pathset = N.getPathsetString();
    	  timeset = N.getTimesetString();
    	  nodes.add(new SGNode(label,placeNo,pathset,timeset));
    	  
      }
      for(int i = 0; i < arclist.size() ; i++){
    	 
    	  SPArc A = new SPArc();
    	  A = arclist.get(i);
    	  int fromIndex  = placeIndex.get(A.getFrom()).intValue();
    	  int toIndex = placeIndex.get(A.getTo()).intValue();
    	  int transition = A.getTransNo();
    	  String text = dataLayer.getArcText(transition);
    	  
    	  edges.add(new TextEdge(
                  (DefaultNode)(nodes.get(fromIndex)),
                  (DefaultNode)(nodes.get(toIndex)),
                   text));
    	  
    	  
      }
      
     
      
      graph.addElements(nodes, edges);
      
      
      return graph;
    } 
   public static String generateNodetable(DataLayer dataLayer){
	      ArrayList res = new ArrayList();
	      String label ; // may need intialize
	      String placeNo;
	      String timeset;
	      String pathset;
	   if(nodelist.size() == 0){
		   mapInitialize(dataLayer);
	   }
	   else if(res.size() < arclist.size()){
		   res.add("Nodes");
		   res.add("Corresponding to");
		   res.add("Path sets");
		   res.add("Time sets");
		   
		   for(int i = 0; i < nodelist.size(); i++ ){
			   SPNode  N = nodelist.get(i).deepCopy();
		    	  // N =  nodelist.get(i);
		    	  //label = String.valueOf(i);
			   label = "Node"+i;
			     placeNo = dataLayer.getPlace(N.getPlaceNo()).getName();
		    	 // placeNo = N.getPlaceNoString();
		    	  pathset = N.getPathsetString();
		    	  timeset = N.getTimesetString();
		    	   res.add(label);
				   res.add(placeNo);
				   res.add(pathset);
				   res.add(timeset);
		   }		   		   
		   
	   }
	   return ResultsHTMLPane.makeTable(res.toArray(),4,false,true,true,true);
   }
   public static String generateArctable(DataLayer dataLayer){
	   if(arclist.size() == 0){
		  // arclist.clear();
		   mapInitialize(dataLayer);
		  
	   }
	   
	   ArrayList <String>res = new ArrayList<String>();
	   res.add("Arc");
	   res.add("From Place");
	   res.add("To Place");
	   res.add("Arc Info");
	   String arclabel;
	   String arcfrom;
	   String arcto;
	   String text;
	  
	   for(int i = 0; i < arclist.size() ; i++){
	    	 
	    	  SPArc A = new SPArc();
	    	  A = arclist.get(i);
	    	  arclabel = "Arc"+ i;
	    	  arcfrom = dataLayer.getPlace(A.getFrom()).getName();
	    	  arcto = dataLayer.getPlace(A.getTo()).getName();
	    	  int transition = A.getTransNo();
	    	  text = dataLayer.getArcText(transition);
	    	  res.add(arclabel);
	    	  res.add(arcfrom);
	    	  res.add(arcto);
	    	  res.add(text);	    	 	    	  	    	  
	      }
	      return ResultsHTMLPane.makeTable(res.toArray(),4,false,true,true,true);
   }
}
