package pipe.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
//����
import pipe.modules.reachability.ReachabilityGraphGenerator;
import javax.swing.event.CaretListener;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.defaults.TextEdge;
import pipe.dataLayer.DataLayer;
//���� ����
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import jpowergraph.PIPEInitialState;
import jpowergraph.PIPENode;
import jpowergraph.PIPEState;
import jpowergraph.PIPETangibleState;
import jpowergraph.PIPEVanishingState;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.defaults.DefaultEdge;
import net.sourceforge.jpowergraph.defaults.DefaultGraph;
import net.sourceforge.jpowergraph.defaults.DefaultNode;
import net.sourceforge.jpowergraph.defaults.TextEdge;

import pipe.dataLayer.DataLayer;
import pipe.dataLayer.DataLayerWriter;
import pipe.dataLayer.NormalArc;
import pipe.dataLayer.PNMLTransformer;
import pipe.dataLayer.PetriNetObject;
import pipe.dataLayer.Place;
import pipe.dataLayer.PlaceTransitionObject;
import pipe.dataLayer.TNTransformer;
import pipe.dataLayer.Transition;
import pipe.experiment.editor.gui.ExperimentEditor;
import pipe.experiment.Experiment;
import pipe.gui.action.GuiAction;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.FileBrowser;
import pipe.gui.widgets.GraphFrame;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.gui.widgets.ResultsMTsPane;
import pipe.io.JarUtilities;
import pipe.io.TransitionRecord;


/**
 * @author Edwin Chung changed the code so that the firedTransitions array list
 * is reset when the animation mode is turned off
 *
 * @author Ben Kirby, 10 Feb 2007: Changed the saveNet() method so that it calls
 * new DataLayerWriter class and passes in current net to save.
 *
 * @author Ben Kirby, 10 Feb 2007: Changed the createNewTab method so that it
 * loads an XML file using the new PNMLTransformer class and createFromPNML
 * DataLayer method.
 *
 * @author Edwin Chung modifed the createNewTab method so that it assigns the
 * file name of the newly created DataLayer object in the dataLayer class
 * (Mar 2007)
 *
 * @author Oliver Haggarty modified initaliseActions to fix a bug that meant
 * not all example nets were loaded if there was a non .xml file in the folder
 */
public class GuiFrame
        extends JFrame
        implements ActionListener, Observer {
   
   // for zoom combobox and dropdown
   private final String[] zoomExamples = {"40%","60%","80%","100%","120%",
   "140%","160%","180%","200%","300%"};
   private String frameTitle;  //Frame title
   private DataLayer appModel;
   private GuiFrame appGui;
   private GuiView appView;
   private int mode, prev_mode, old_mode;             // *** mode WAS STATIC ***
   private int newNameCounter = 1;
   private JTabbedPane appTab;
   private StatusBar statusBar;
   private JMenuBar menuBar;
   private JToolBar animationToolBar, drawingToolBar;
   
   //���� 
   public	java.util.ArrayList<List> arraynode = new ArrayList<List>();
   public java.util.ArrayList<List> arrayedge = new ArrayList<List>();
   private double limittime;
   public double sumStartTime=0;
   public double sumEndTime=0;
   //���ڵĵ��������̸���
   public double sernum;  
   //���ڵõ��������̵�transition�����֡�
   public String str = "";
   public double getLimittime() {
	return limittime;
}
   


public void setLimittime(double limittime) {
	this.limittime = limittime;
}
//���� ����

//private Map actions = new HashMap();
   private JComboBox zoomComboBox;
   
   
   private FileAction createAction, openAction, closeAction, saveAction,
           saveAsAction, exitAction, printAction, exportPNGAction,
           exportTNAction, exportPSAction, importAction;
   
   private EditAction copyAction, cutAction, pasteAction, undoAction, redoAction;
   private GridAction toggleGrid;
   private ZoomAction zoomOutAction, zoomInAction, zoomAction;
   private DeleteAction deleteAction;
   private TypeAction annotationAction, arcAction, inhibarcAction, placeAction,
           transAction, timedtransAction, tokenAction, selectAction, rateAction,
           markingAction, deleteTokenAction, dragAction,addTimeIntervalTransition;
   private AnimateAction startAction, stepforwardAction, stepbackwardAction,
           randomAction, randomAnimateAction;
   
 //du: ����MRG analysis�˵���
   private MRGAction MTAction, ComposeAction, DecomposeAction,
                     ConstructAction,  AnalyzeAction, limitFrame, Analysis1Action,limitAction;
   
     public boolean dragging = false;
   
   private HelpBox helpAction;
   private ExperimentAction loadExperimentAction, experimentEditorAction;
   
   
   private boolean editionAllowed = true;
   
   private CopyPasteManager copyPasteManager;
   
   
   public GuiFrame(String title) {
      // HAK-arrange for frameTitle to be initialized and the default file name
      // to be appended to basic window title
      frameTitle = title;
      setTitle(null);
      
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception exc) {
         System.err.println("Error loading L&F: " + exc);
      }
      
      this.setIconImage(new ImageIcon(
              Thread.currentThread().getContextClassLoader().getResource(
              CreateGui.imgPath + "icon.png")).getImage());
      
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      this.setSize(screenSize.width * 80/100, screenSize.height  * 80/100);
      this.setLocationRelativeTo(null);
      
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      
      buildMenus();
      
      // Status bar...
      statusBar = new StatusBar();
      getContentPane().add(statusBar, BorderLayout.PAGE_END);
      
      // Build menus
      buildToolbar();
      
      addWindowListener(new WindowHandler());
      
      //
      copyPasteManager = new CopyPasteManager();
      
      this.setForeground(java.awt.Color.BLACK);
      this.setBackground(java.awt.Color.WHITE);
   }
   
   
   /**
    * This method does build the menus.
    *
    * @author unknown
    *
    * @author Dave Patterson - fixed problem on OSX due to invalid character
    * in URI caused by unescaped blank. The code changes one blank character
    * if it exists in the string version of the URL. This way works safely in
    * both OSX and Windows. I also added a printStackTrace if there is an
    * exception caught in the setup for the "Example nets" folder.
    **/
   private void buildMenus() {
      menuBar = new JMenuBar();
      
      JMenu fileMenu = new JMenu("File");
      fileMenu.setMnemonic('F');
      
      addMenuItem(fileMenu, createAction =
              new FileAction("New",    "Create a new Petri net","ctrl N"));
      addMenuItem(fileMenu, openAction   =
              new FileAction("Open",   "Open","ctrl O"));
      addMenuItem(fileMenu, closeAction  =
              new FileAction("Close",  "Close the current tab","ctrl W"));
      fileMenu.addSeparator();
      addMenuItem(fileMenu, importAction  =
              new FileAction("Import",  "Import from Timenet",""));
      addMenuItem(fileMenu, saveAction   =
              new FileAction("Save",   "Save","ctrl S"));
      addMenuItem(fileMenu, saveAsAction =
              new FileAction("Save as","Save as...","shift ctrl S"));
      
      // Export menu
      JMenu exportMenu=new JMenu("Export");
      exportMenu.setIcon(
              new ImageIcon(Thread.currentThread().getContextClassLoader().
              getResource(CreateGui.imgPath + "Export.png")));
      addMenuItem(exportMenu, exportPNGAction  =
              new FileAction("PNG", "Export the net to PNG format","ctrl G"));
      addMenuItem(exportMenu, exportPSAction  =
              new FileAction("PostScript", "Export the net to PostScript format","ctrl T"));
      addMenuItem(exportMenu, exportTNAction =
              new FileAction("Timenet","Export the net to Timenet format",""));
      fileMenu.add(exportMenu);
      fileMenu.addSeparator();
      addMenuItem(fileMenu, printAction  =
              new FileAction("Print",  "Print","ctrl P"));
      fileMenu.addSeparator();
      
      // Example files menu
      try {
         URL examplesDirURL = Thread.currentThread().getContextClassLoader().
                 getResource("Example nets" + System.getProperty("file.separator"));
         
         if (JarUtilities.isJarFile(examplesDirURL)){
            
            JarFile jarFile = new JarFile(JarUtilities.getJarName(examplesDirURL));
            
            ArrayList <JarEntry> nets =
                    JarUtilities.getJarEntries(jarFile, "Example nets");
            
            Arrays.sort(nets.toArray(), new Comparator(){
               public int compare(Object one, Object two) {
                  return ((JarEntry)one).getName().compareTo(((JarEntry)two).getName());
               }
            });
            
            if (nets.size() > 0) {
               JMenu exampleMenu=new JMenu("Example nets");
               exampleMenu.setIcon(
                       new ImageIcon(Thread.currentThread().getContextClassLoader().
                       getResource(CreateGui.imgPath + "Example.png")));
               int index = 0;
               for (int i = 0; i < nets.size(); i++){
                  if (nets.get(i).getName().toLowerCase().endsWith(".xml")){
                     addMenuItem(exampleMenu,
                             new ExampleFileAction(nets.get(i),
                             (index < 10) ?("ctrl " + index) :null));
                     index++;
                  }
               }
               fileMenu.add(exampleMenu);
               fileMenu.addSeparator();
            }
         } else {
            File examplesDir = new File(examplesDirURL.toURI());
            /**
             * The next block fixes a problem that surfaced on Mac OSX with
             * PIPE 2.4. In that environment (and not in Windows) any blanks
             * in the project name in Eclipse are property converted to '%20'
             * but the blank in "Example nets" is not. The following code
             * will do nothing on a Windows machine or if the logic on OSX
             * changess. I also added a stack trace so if the problem
             * occurs for another environment (perhaps multiple blanks need
             * to be manually changed) it can be easily fixed.  DP
             */
            // examplesDir = new File(new URI(examplesDirURL.toString()));
            String dirURLString = examplesDirURL.toString();
            int index = dirURLString.indexOf( " " );
            if ( index > 0 ) {
               StringBuffer sb = new StringBuffer( dirURLString );
               sb.replace( index, index + 1, "%20" );
               dirURLString = sb.toString();
            }
            
            examplesDir = new File( new URI(dirURLString ) );
            
            File[] nets = examplesDir.listFiles();
            
            Arrays.sort(nets,new Comparator(){
               public int compare(Object one, Object two) {
                  return ((File)one).getName().compareTo(((File)two).getName());
               }
            });
            
            // Oliver Haggarty - fixed code here so that if folder contains non
            // .xml file the Example x counter is not incremented when that file
            // is ignored
            if (nets.length > 0) {
               JMenu exampleMenu=new JMenu("Example nets");
               exampleMenu.setIcon(
                       new ImageIcon(Thread.currentThread().getContextClassLoader().
                       getResource(CreateGui.imgPath + "Example.png")));
               int k = 0;
               for (int i = 0; i < nets.length; i++){
                  if(nets[i].getName().toLowerCase().endsWith(".xml")){
                     addMenuItem(exampleMenu,
                             new ExampleFileAction(nets[i], (k<10)?"ctrl " + (k++) :null));
                  }
               }
               fileMenu.add(exampleMenu);
               fileMenu.addSeparator();
            }
         }
      } catch (Exception e) {
         System.err.println("Error getting example files:" + e);
         e.printStackTrace();
      }
      addMenuItem(fileMenu, exitAction =
              new FileAction("Exit", "Close the program", "ctrl Q"));
      
      JMenu editMenu = new JMenu("Edit");
      editMenu.setMnemonic('E');
      addMenuItem(editMenu, undoAction =
              new EditAction("Undo", "Undo (Ctrl-Z)", "ctrl Z"));
      addMenuItem(editMenu, redoAction =
              new EditAction("Redo", "Redo (Ctrl-Y)","ctrl Y"));
      editMenu.addSeparator();
      addMenuItem(editMenu, cutAction =
              new EditAction("Cut", "Cut (Ctrl-X)","ctrl X"));
      addMenuItem(editMenu, copyAction =
              new EditAction("Copy", "Copy (Ctrl-C)","ctrl C"));
      addMenuItem(editMenu, pasteAction =
              new EditAction("Paste", "Paste (Ctrl-V)","ctrl V"));
      addMenuItem(editMenu, deleteAction =
              new DeleteAction("Delete", "Delete selection","DELETE"));
      
      JMenu drawMenu = new JMenu("Draw");
      drawMenu.setMnemonic('D');
      addMenuItem(drawMenu, selectAction =
              new TypeAction("Select", Pipe.SELECT, "Select components","S",true));
      drawMenu.addSeparator();
      addMenuItem(drawMenu, placeAction =
              new TypeAction("Place", Pipe.PLACE, "Add a place","P",true));
      addMenuItem(drawMenu, transAction =
              new TypeAction("Immediate transition", Pipe.IMMTRANS,
              "Add an immediate transition","I",true));
      addMenuItem(drawMenu, timedtransAction  =
              new TypeAction("Timed transition", Pipe.TIMEDTRANS,
              "Add a timed transition","T",true));
            addMenuItem(drawMenu, arcAction =
              new TypeAction("Arc", Pipe.ARC, "Add an arc","A",true));
      addMenuItem(drawMenu, inhibarcAction =
              new TypeAction("Inhibitor Arc", Pipe.INHIBARC,
              "Add an inhibitor arc", "H",true));
      addMenuItem(drawMenu, annotationAction =
              new TypeAction("Annotation", Pipe.ANNOTATION,
              "Add an annotation","N",true));
      drawMenu.addSeparator();
      addMenuItem(drawMenu, tokenAction =
              new TypeAction("Add token", Pipe.ADDTOKEN, "Add a token", "ADD", true));
      addMenuItem(drawMenu, deleteTokenAction =
              new TypeAction("Delete token", Pipe.DELTOKEN, "Delete a token",
              "SUBTRACT",true));
      drawMenu.addSeparator();
      addMenuItem(drawMenu, rateAction =
              new TypeAction("Rate Parameter", Pipe.RATE, "Rate Parameter",
              "R",true));
      addMenuItem(drawMenu, markingAction =
              new TypeAction("Marking Parameter", Pipe.MARKING, "Marking Parameter",
              "M",true));
      
      JMenu viewMenu = new JMenu("View");
      viewMenu.setMnemonic('V');
      
      JMenu zoomMenu=new JMenu("Zoom");
      zoomMenu.setIcon(
              new ImageIcon(Thread.currentThread().getContextClassLoader().
              getResource(CreateGui.imgPath + "Zoom.png")));
      addZoomMenuItems(zoomMenu);
      
      addMenuItem(viewMenu, zoomOutAction =
              new ZoomAction("Zoom out","Zoom out by 10% ", "ctrl MINUS"));
      addMenuItem(viewMenu, zoomInAction =
              new ZoomAction("Zoom in","Zoom in by 10% ", "ctrl PLUS"));
      viewMenu.add(zoomMenu);
      
      viewMenu.addSeparator();
      addMenuItem(viewMenu, toggleGrid =
              new GridAction("Cycle grid", "Change the grid size", "G"));
      addMenuItem(viewMenu, dragAction =
              new TypeAction("Drag", Pipe.DRAG, "Drag the drawing", "D", true));
      
      JMenu animateMenu = new JMenu("Animate");
      animateMenu.setMnemonic('A');
      addMenuItem(animateMenu, startAction =
              new AnimateAction("Animation mode", Pipe.START,
              "Toggle Animation Mode", "Ctrl A", true));
      animateMenu.addSeparator();
      addMenuItem(animateMenu, stepbackwardAction =
              new AnimateAction("Back", Pipe.STEPBACKWARD,
              "Step backward a firing", "4"));
      addMenuItem(animateMenu, stepforwardAction  =
              new AnimateAction("Forward", Pipe.STEPFORWARD,
              "Step forward a firing", "6"));
      addMenuItem(animateMenu, randomAction =
              new AnimateAction("Random", Pipe.RANDOM,
              "Randomly fire a transition", "5"));
      addMenuItem(animateMenu, randomAnimateAction =
              new AnimateAction("Animate", Pipe.ANIMATE,
              "Randomly fire a number of transitions", "7",true));
    
      // du: ��ʼ���MRG Analysis�˵�
      JMenu MRGMenu = new JMenu("MRG");
      MRGMenu.setMnemonic('M');
      addMenuItem(MRGMenu, MTAction = 
    	  new MRGAction("Add MTs", Pipe.MT, "Add mediation transitions","ctrl A",true));
      MRGMenu.addSeparator();
      addMenuItem(MRGMenu, ComposeAction =
          new MRGAction("Compose", Pipe.COMPOSE, "Compose OWFNs by MTs","ctrl C",true));
      MRGMenu.addSeparator();
      addMenuItem(MRGMenu, DecomposeAction =
          new MRGAction("Decompose", Pipe.DECOMPOSE, "Decompose OWFNs by MTs","ctrl D",true));
      MRGMenu.addSeparator();
      addMenuItem(MRGMenu, ConstructAction  =
          new MRGAction("Construct", Pipe.CONSTRUCT, "Construct the MRG","ctrl shift C",true));
      MRGMenu.addSeparator();
      addMenuItem(MRGMenu, AnalyzeAction =
          new MRGAction("Analyze", Pipe.ANALYZE, "Results by MRG","ctrl shift A",true));
      drawMenu.addSeparator(); 
      addMenuItem(MRGMenu, limitFrame = 
          new MRGAction("���ʱ������", Pipe.LIMITFRAME, "time limit","ctrl shift .",true));
      drawMenu.addSeparator();
      addMenuItem(MRGMenu, Analysis1Action = 
          new MRGAction("�������", Pipe.ANALYZE1, "time analysis","ctrl shift ,",true));
      drawMenu.addSeparator();
    // du: MRG Analysis�˵������� 
      
      
      JMenu helpMenu = new JMenu("Help");
      helpMenu.setMnemonic('H');
      helpAction = new HelpBox("Help", "View documentation", "F1", "index.htm");
      addMenuItem(helpMenu,helpAction);
      JMenuItem aboutItem = helpMenu.add("About PIPE");
      aboutItem.addActionListener(this); // Help - About is implemented differently
      
      URL iconURL = Thread.currentThread().getContextClassLoader().
              getResource(CreateGui.imgPath + "About.png");
      if (iconURL != null) {
         aboutItem.setIcon(new ImageIcon(iconURL));
      }
      
      JMenu experimentMenu = new JMenu("Experiment");
      addMenuItem(experimentMenu, loadExperimentAction =
              new ExperimentAction("Load experiment", "Load an experiment file",""));
      addMenuItem(experimentMenu, experimentEditorAction =
              new ExperimentAction("Experiment Editor", "Start the experiment editor",""));
      
      menuBar.add(fileMenu);
      menuBar.add(editMenu);
      menuBar.add(viewMenu);
      menuBar.add(drawMenu);
      menuBar.add(animateMenu);
      menuBar.add(MRGMenu);
      //menuBar.add(experimentMenu);
      menuBar.add(helpMenu);
      setJMenuBar(menuBar);
   }
   
   
   private void buildToolbar() {
      // Create the toolbar
      JToolBar toolBar = new JToolBar();
      toolBar.setFloatable(false);//Inhibit toolbar floating
      
      addButton(toolBar,createAction);
      addButton(toolBar,openAction);
      addButton(toolBar,saveAction);
      addButton(toolBar,saveAsAction);
      addButton(toolBar,closeAction);
      toolBar.addSeparator();
      addButton(toolBar,printAction);
      toolBar.addSeparator();
      addButton(toolBar,cutAction);
      addButton(toolBar,copyAction);
      addButton(toolBar,pasteAction);
      addButton(toolBar,deleteAction);
      addButton(toolBar,undoAction);
      addButton(toolBar,redoAction);
      toolBar.addSeparator();
      
      addButton(toolBar, zoomOutAction);
      addZoomComboBox(toolBar,
              zoomAction = new ZoomAction("Zoom","Select zoom percentage ", ""));
      addButton(toolBar, zoomInAction);
      toolBar.addSeparator();
      
      //du: ���￪ʼ�����MRG��ص�toolbar��
      addButton(toolBar,MTAction);
      addButton(toolBar,ComposeAction);
      addButton(toolBar,DecomposeAction);
      addButton(toolBar,ConstructAction);
      addButton(toolBar,AnalyzeAction);  
      toolBar.addSeparator();
      //du����ӽ�����
      
      //���� ���һ�������interval��transition�İ�ť��
//      addButton(toolBar, addIntervalTransitionAction);
      addButton(toolBar, toggleGrid);
      addButton(toolBar, dragAction);
      addButton(toolBar, startAction);
      
      drawingToolBar = new JToolBar();
      drawingToolBar.setFloatable(false);
      
      toolBar.addSeparator();
      addButton(drawingToolBar,selectAction);
      drawingToolBar.addSeparator();
      addButton(drawingToolBar,placeAction);//Add Draw Menu Buttons
      addButton(drawingToolBar,transAction);
      addButton(drawingToolBar,timedtransAction);
      addButton(drawingToolBar,arcAction);
      addButton(drawingToolBar,inhibarcAction);
      addButton(drawingToolBar,annotationAction);
      drawingToolBar.addSeparator();
      addButton(drawingToolBar,tokenAction);
      addButton(drawingToolBar,deleteTokenAction);
      drawingToolBar.addSeparator();
      addButton(drawingToolBar,rateAction);
      addButton(drawingToolBar,markingAction);
      
      toolBar.add(drawingToolBar);
      
      animationToolBar = new JToolBar();
      animationToolBar.setFloatable(false);
      addButton(animationToolBar, stepbackwardAction);
      addButton(animationToolBar, stepforwardAction);
      addButton(animationToolBar, randomAction);
      addButton(animationToolBar, randomAnimateAction);
      
      toolBar.add(animationToolBar);
      animationToolBar.setVisible(false);
      
      toolBar.addSeparator();
      addButton(toolBar,helpAction);
      
      for(int i=0;i<toolBar.getComponentCount();i++){
         toolBar.getComponent(i).setFocusable(false);
      }
      
      getContentPane().add(toolBar,BorderLayout.PAGE_START);
   }
   
   
   private void addButton(JToolBar toolBar, GuiAction action) {
      
      if (action.getValue("selected") != null) {
         toolBar.add(new ToggleButton(action));
      } else {
         toolBar.add(action);
      }
   }
   
   
   /**
    * @author Ben Kirby
    * Takes the method of setting up the Zoom menu out of the main buildMenus method.
    * @param JMenu - the menu to add the submenu to
    */
   private void addZoomMenuItems(JMenu zoomMenu) {
      for(int i=0; i <= zoomExamples.length-1; i++) {
         JMenuItem newItem = new JMenuItem(
                 new ZoomAction(zoomExamples[i],"Select zoom percentage",
                 i<10 ? "ctrl shift " + i: ""));
         zoomMenu.add(newItem);
      }
   }
   
   
   /**
    * @author Ben Kirby
    * Just takes the long-winded method of setting up the ComboBox out of the
    * main buildToolbar method.
    * Could be adapted for generic addition of comboboxes
    * @param toolBar the JToolBar to add the button to
    * @param action the action that the ZoomComboBox performs
    */
   private void addZoomComboBox(JToolBar toolBar, Action action) {
      Dimension zoomComboBoxDimension = new Dimension(65,28);
      zoomComboBox = new JComboBox(zoomExamples);
      zoomComboBox.setEditable(true);
      zoomComboBox.setSelectedItem("100%");
      zoomComboBox.setMaximumRowCount(zoomExamples.length);
      zoomComboBox.setMaximumSize(zoomComboBoxDimension);
      zoomComboBox.setMinimumSize(zoomComboBoxDimension);
      zoomComboBox.setPreferredSize(zoomComboBoxDimension);
      zoomComboBox.setAction(action);
      toolBar.add(zoomComboBox);
   }
   
   
   private JMenuItem addMenuItem(JMenu menu, Action action){
      JMenuItem item = menu.add(action);
      KeyStroke keystroke = (KeyStroke)action.getValue(Action.ACCELERATOR_KEY);
      
      if (keystroke != null) {
         item.setAccelerator(keystroke);
      }
      return item;
   }
   
   
   /* sets all buttons to enabled or disabled according to status. */
   private void enableActions(boolean status){
      
      saveAction.setEnabled(status);
      saveAsAction.setEnabled(status);
      
      placeAction.setEnabled(status);
      arcAction.setEnabled(status);
      inhibarcAction.setEnabled(status);
      annotationAction.setEnabled(status);
      transAction.setEnabled(status);
      timedtransAction.setEnabled(status);
      //addIntervalTransitionAction.setEnabled(status);
      tokenAction.setEnabled(status);
      deleteAction.setEnabled(status);
      selectAction.setEnabled(status);
      deleteTokenAction.setEnabled(status);
      rateAction.setEnabled(status);
      markingAction.setEnabled(status);
      
//      toggleGrid.setEnabled(status);
      
      if (status) {
         startAction.setSelected(false);
         randomAnimateAction.setSelected(false);
         stepbackwardAction.setEnabled(!status);
         stepforwardAction.setEnabled(!status);
         drawingToolBar.setVisible(true);
         animationToolBar.setVisible(false);
      }
      randomAction.setEnabled(!status);
      randomAnimateAction.setEnabled(!status);
      
      if (!status){
         drawingToolBar.setVisible(false);
         animationToolBar.setVisible(true);
         pasteAction.setEnabled(status);
         undoAction.setEnabled(status);
         redoAction.setEnabled(status);
      } else {
         pasteAction.setEnabled(getCopyPasteManager().pasteEnabled());
      }
      copyAction.setEnabled(status);
      cutAction.setEnabled(status);
      deleteAction.setEnabled(status);
   }
   
   
   //set frame objects by array index
   private void setObjects(int index){
      appModel = CreateGui.getModel(index);
      appView = CreateGui.getView(index);
   }
   
   
   //HAK set current objects in Frame
   public void setObjects(){
      appModel = CreateGui.getModel();
      appView = CreateGui.getView();
   }
   
   
   private void setObjectsNull(int index){
      CreateGui.removeTab(index);
   }
   
   
   // set tabbed pane properties and add change listener that updates tab with
   // linked model and view
   public void setTab(){
      
      appTab = CreateGui.getTab();
      appTab.addChangeListener(new ChangeListener() {
         
         public void stateChanged(ChangeEvent e) {
            
            if (appGui.getCopyPasteManager().pasteInProgress()) {
               appGui.getCopyPasteManager().cancelPaste();
            }
            
            int index = appTab.getSelectedIndex();
            setObjects(index);
            if (appView != null) {
               appView.setVisible(true);
               appView.repaint();
               updateZoomCombo();
               
               enableActions(!appView.isInAnimationMode());
               //CreateGui.getAnimator().restoreModel();
               //CreateGui.removeAnimationHistory();
               
               setTitle(appTab.getTitleAt(index));
               
               // TODO: change this code... it's ugly :)
               if (appGui.getMode() == Pipe.SELECT) {
                  appGui.init();
               }
               
            } else {
               setTitle(null);
            }
            
         }
         
      });
      appGui = CreateGui.getApp();
      appView = CreateGui.getView();
   }
   
   
   // Less sucky yet far, far simpler to code About dialogue
   public void actionPerformed(ActionEvent e){
      
      JOptionPane.showMessageDialog(this,
              "Imperial College DoC MSc Group And MSc Individual Project\n\n" +
              "Original version PIPE(c)\n2003 by Jamie Bloom, Clare Clark, Camilla Clifford, Alex Duncan, Haroun Khan and Manos Papantoniou\n\n" +
              "MLS(tm) Edition PIPE2(c)\n2004 by Tom Barnwell, Michael Camacho, Matthew Cook, Maxim Gready, Peter Kyme and Michail Tsouchlaris\n" +
              "2005 by Nadeem Akharware\n\n" +
              "PIPE 2.4 by Tim Kimber, Ben Kirby, Thomas Master, " +
              "Matthew Worthington\n\n" +
              "PIPE 2.5 by Pere Bonet (Universitat de les Illes Balears)\n\n" +              
              "http://pipe2.sourceforge.net/",
              "About PIPE2",
              JOptionPane.INFORMATION_MESSAGE);
   }
   
   
   // HAK Method called by netModel object when it changes
   public void update(Observable o, Object obj){
      if ((mode != Pipe.CREATING) && (!appView.isInAnimationMode())) {
         appView.setNetChanged(true);
      }
   }
   
   
   public void saveOperation(boolean forceSaveAs){
      
      if (appView == null) {
         return;
      }
      
      File modelFile = CreateGui.getFile();
      if (!forceSaveAs && modelFile != null) { // ordinary save
/*
      //Disabled as currently ALWAYS prevents the net from being saved - Nadeem 26/05/2005
         if (!appView.netChanged) {
            return;
         }
 */
         saveNet(modelFile);
      } else {                              // save as
         String path = null;
         if (modelFile != null) {
            path = modelFile.toString();
         } else {
            path = appTab.getTitleAt(appTab.getSelectedIndex());
         }
         String filename = new FileBrowser(path).saveFile();
         if (filename != null) {
            saveNet(new File(filename));
         }
      }
   }
   
   
   private void saveNet(File outFile){
      try{
         // BK 10/02/07:
         // changed way of saving to accomodate new DataLayerWriter class
         DataLayerWriter saveModel = new DataLayerWriter(appModel);
         saveModel.savePNML(outFile);
         //appModel.savePNML(outFile);
         
         CreateGui.setFile(outFile,appTab.getSelectedIndex());
         appView.setNetChanged(false);
         appTab.setTitleAt(appTab.getSelectedIndex(),outFile.getName());
         setTitle(outFile.getName());  // Change the window title
         appView.getUndoManager().clear();
         undoAction.setEnabled(false);
         redoAction.setEnabled(false);
      } catch (Exception e) {
         System.err.println(e);
         JOptionPane.showMessageDialog(GuiFrame.this,
                 e.toString(),
                 "File Output Error",
                 JOptionPane.ERROR_MESSAGE);
         return;
      }
   }
   
   
   /**
    * Creates a new tab with the selected file, or a new file if filename==null
    * @param filename Filename of net to load, or <b>null</b> to create a new,
    *                 empty tab
    */
   public void createNewTab(File file, boolean isTN) {
      int freeSpace = CreateGui.getFreeSpace();
      String name="";
      
      // if we are in the middle of a paste action, we cancel it because we will
      // create a new tab now
      if (this.getCopyPasteManager().pasteInProgress()) {
         this.getCopyPasteManager().cancelPaste();
      }
      
      setObjects(freeSpace);
      
      appModel.addObserver((Observer)appView); // Add the view as Observer
      appModel.addObserver((Observer)appGui);  // Add the app window as observer
      
      if (file == null) {
         name = "New Petri net " + (newNameCounter++) + ".xml";
      } else {
         try {
            //BK 10/02/07: Changed loading of PNML to accomodate new
            //PNMLTransformer class
            CreateGui.setFile(file,freeSpace);
            
         
            if (isTN) {
               TNTransformer transformer=new TNTransformer();
               appModel.createFromPNML(
                       transformer.transformTN(file.getPath()));
            } else {
               //ProgressBar progressBar = new ProgressBar(file.getName());
               //Thread t = new Thread(progressBar);
               //t.start();
               PNMLTransformer transformer = new PNMLTransformer();
               appModel.createFromPNML(
                       transformer.transformPNML(file.getPath()));
               //progressBar.exit();
               appView.scrollRectToVisible(new Rectangle(0,0,1,1));
            }
            
            CreateGui.setFile(file,freeSpace);
            name = file.getName();
         } catch(Exception e) {
            JOptionPane.showMessageDialog(
                    GuiFrame.this,
                    "Error loading file:\n" + name + "\nGuru meditation:\n"
                    + e.toString(),
                    "File load error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
         }
      }
      
      appView.setNetChanged(false);   // Status is unchanged
      
      JScrollPane scroller = new JScrollPane(appView);
      // make it less bad on XP
      scroller.setBorder(new BevelBorder(BevelBorder.LOWERED));
      appTab.addTab(name,null,scroller,null);
      appTab.setSelectedIndex(freeSpace);
      
      appView.updatePreferredSize();
      //appView.add( new ViewExpansionComponent(appView.getWidth(),
      //        appView.getHeight());
      
      setTitle(name);// Change the program caption
      appTab.setTitleAt(freeSpace, name);
      selectAction.actionPerformed(null);
   }
   
   
   /**Loads an Experiment XML file and shows a suitable message in case of error.
    * @param path the absolute path to the experiment file.
    */
   private void loadExperiment(String path){
      Experiment exp = new Experiment(path,appModel);
      try{
         exp.Load();
      } catch(org.xml.sax.SAXParseException spe) {
         //if the experiment file does not fit the schema.
         JOptionPane.showMessageDialog(GuiFrame.this,
                 "The Experiment file is not valid."+
                 System.getProperty("line.separator")+
                 "Line "+spe.getLineNumber()+": "+
                 spe.getMessage(),
                 "Experiment Input Error",
                 JOptionPane.ERROR_MESSAGE);
      } catch(pipe.experiment.validation.NotMatchingException nme) {
         //if the experiment file does not match with the current net.
         JOptionPane.showMessageDialog(GuiFrame.this,
                 "The Experiment file is not valid."+
                 System.getProperty("line.separator")+
                 nme.getMessage(),
                 "Experiment Input Error",
                 JOptionPane.ERROR_MESSAGE);
      } catch(pipe.experiment.InvalidExpressionException iee) {
         JOptionPane.showMessageDialog(GuiFrame.this,
                 "The Experiment file is not valid."+
                 System.getProperty("line.separator")+
                 iee.getMessage(),
                 "Experiment Input Error",
                 JOptionPane.ERROR_MESSAGE);
      }
   }
   
   
   /**
    * If current net has modifications, asks if you want to save and does it if
    * you want.
    * @return true if handled, false if cancelled
    */
   private boolean checkForSave() {
      
      if (appView.getNetChanged()) {
         int result=JOptionPane.showConfirmDialog(GuiFrame.this,
                 "Current file has changed. Save current file?",
                 "Confirm Save Current File",
                 JOptionPane.YES_NO_CANCEL_OPTION,
                 JOptionPane.WARNING_MESSAGE);
         switch(result) {
            case JOptionPane.YES_OPTION:
               saveOperation(false);
               break;
            case JOptionPane.CLOSED_OPTION:
            case JOptionPane.CANCEL_OPTION:
               return false;
         }
      }
      return true;
   }
   
   
   /**
    * If current net has modifications, asks if you want to save and does it if
    * you want.
    * @return true if handled, false if cancelled
    */
   private boolean checkForSaveAll(){
      // Loop through all tabs and check if they have been saved
      for (int counter = 0; counter < appTab.getTabCount(); counter++) {
         appTab.setSelectedIndex(counter);
         if (checkForSave() == false){
            return false;
         }
      }
      return true;
   }
   
   
   public void setRandomAnimationMode(boolean on) {
      if (on == false){
         stepforwardAction.setEnabled(
                 CreateGui.getAnimationHistory().isStepForwardAllowed());
         stepbackwardAction.setEnabled(
                 CreateGui.getAnimationHistory().isStepBackAllowed());
      } else {
         stepbackwardAction.setEnabled(false);
         stepforwardAction.setEnabled(false);
      }
      randomAction.setEnabled(!on);
      randomAnimateAction.setSelected(on);
   }
   
   
   private void setAnimationMode(boolean on) {
      randomAnimateAction.setSelected(false);
      CreateGui.getAnimator().setNumberSequences(0);
      startAction.setSelected(on);
      CreateGui.getView().changeAnimationMode(on);
      if (on) {
         CreateGui.getAnimator().storeModel();
         CreateGui.currentPNMLData().setEnabledTransitions();
         CreateGui.getAnimator().highlightEnabledTransitions();
         CreateGui.addAnimationHistory();
         enableActions(false);//disables all non-animation buttons
         setEditionAllowed(false);
         statusBar.changeText(statusBar.textforAnimation);
      } else {
         setEditionAllowed(true);
         statusBar.changeText(statusBar.textforDrawing);
         CreateGui.getAnimator().restoreModel();
         CreateGui.removeAnimationHistory();
         enableActions(true); //renables all non-animation buttons
      }
   }
   
   
   public void resetMode(){
      setMode(old_mode);
   }
   
   
   public void setFastMode(int _mode){
      old_mode = mode;
      setMode(_mode);
   }
   
   
   public void setMode(int _mode) {
      // Don't bother unless new mode is different.
      if (mode != _mode) {
         prev_mode = mode;
         mode = _mode;
      }
   }
   
   
   public int getMode() {
      return mode;
   }
   
   
   
   public void restoreMode() {
      mode = prev_mode;
      placeAction.setSelected(mode == Pipe.PLACE);
      transAction.setSelected(mode == Pipe.IMMTRANS);
      timedtransAction.setSelected(mode == Pipe.TIMEDTRANS);
      arcAction.setSelected(mode == Pipe.ARC);
      inhibarcAction.setSelected(mode == Pipe.INHIBARC);
      tokenAction.setSelected(mode == Pipe.ADDTOKEN);
      deleteTokenAction.setSelected(mode == Pipe.DELTOKEN);
      rateAction.setSelected(mode == Pipe.RATE);
      markingAction.setSelected(mode == Pipe.MARKING);
      selectAction.setSelected(mode == Pipe.SELECT);
      annotationAction.setSelected(mode == Pipe.ANNOTATION);
   }
   
   
   public void setTitle(String title) {
      super.setTitle((title == null) ? frameTitle : frameTitle + ": " + title);
   }
   
   
   public boolean isEditionAllowed(){
      return editionAllowed;
   }
   
   
   public void setEditionAllowed(boolean flag){
      editionAllowed = flag;
   }
   
   
   public void setUndoActionEnabled(boolean flag) {
      undoAction.setEnabled(flag);
   }
   
   
   public void setRedoActionEnabled(boolean flag) {
      redoAction.setEnabled(flag);
   }
   
   
   public CopyPasteManager getCopyPasteManager() {
      return copyPasteManager;
   }
   
   
   public void init() {
      // Set selection mode at startup
      setMode(Pipe.SELECT);
      selectAction.actionPerformed(null);
   }
   
   
   /**
    * @author Ben Kirby
    * Remove the listener from the zoomComboBox, so that when the box's selected
    * item is updated to keep track of ZoomActions called from other sources, a
    * duplicate ZoomAction is not called
    */
   public void updateZoomCombo() {
      ActionListener zoomComboListener=(zoomComboBox.getActionListeners())[0];
      zoomComboBox.removeActionListener(zoomComboListener);
      zoomComboBox.setSelectedItem(String.valueOf(appView.getZoomController().getPercent())+"%");
      zoomComboBox.addActionListener(zoomComboListener);
   }
   
   
   public  StatusBar getStatusBar(){
      return statusBar;
   }
   
   
   private Component c = null; //arreglantzoom
   private Component p = new BlankLayer(this);
   /* */
   void hideNet(boolean doHide) {
      if (doHide) {
         c = appTab.getComponentAt(appTab.getSelectedIndex());
         appTab.setComponentAt(appTab.getSelectedIndex(), p);
      } else {
         if (c != null) {
            appTab.setComponentAt(appTab.getSelectedIndex(), c);
            c = null;
         }
      }
      appTab.repaint();
   }
   
   
   
   
   class AnimateAction extends GuiAction {
      
      private int typeID;
      private AnimationHistory animBox;
      
      
      AnimateAction(String name, int typeID, String tooltip, String keystroke){
         super(name, tooltip, keystroke);
         this.typeID = typeID;
      }
      
      
      AnimateAction(String name, int typeID, String tooltip, String keystroke,
              boolean toggleable){
         super(name, tooltip, keystroke, toggleable);
         this.typeID = typeID;
      }
      
      
      public void actionPerformed(ActionEvent ae){
         if (appView == null) {
            return;
         }
         
         animBox = CreateGui.getAnimationHistory();
         
         switch(typeID){
            case Pipe.START:
               try {
                  setAnimationMode(!appView.isInAnimationMode());
                  if (!appView.isInAnimationMode()) {
                     restoreMode();
                     PetriNetObject.ignoreSelection(false);
                  } else {
                     setMode(typeID);
                     PetriNetObject.ignoreSelection(true);
                     // Do we keep the selection??
                     appView.getSelectionObject().clearSelection();
                  }
               } catch (Exception e) {
                  System.err.println(e);
                  JOptionPane.showMessageDialog(GuiFrame.this, e.toString(),
                          "Animation Mode Error", JOptionPane.ERROR_MESSAGE);
                  startAction.setSelected(false);
                  appView.changeAnimationMode(false);
               }
               stepforwardAction.setEnabled(false);
               stepbackwardAction.setEnabled(false);
               break;
               
            case Pipe.RANDOM:
               animBox.clearStepsForward();
               CreateGui.getAnimator().doRandomFiring();
               stepforwardAction.setEnabled(animBox.isStepForwardAllowed());
               stepbackwardAction.setEnabled(animBox.isStepBackAllowed());
               break;
               
            case Pipe.STEPFORWARD:
               animBox.stepForward();
               CreateGui.getAnimator().stepForward();
               stepforwardAction.setEnabled(animBox.isStepForwardAllowed());
               stepbackwardAction.setEnabled(animBox.isStepBackAllowed());
               break;
               
            case Pipe.STEPBACKWARD:
               animBox.stepBackwards();
               CreateGui.getAnimator().stepBack();
               stepforwardAction.setEnabled(animBox.isStepForwardAllowed());
               stepbackwardAction.setEnabled(animBox.isStepBackAllowed());
               break;
               
            case Pipe.ANIMATE:
               Animator a = CreateGui.getAnimator();
               
               if (a.getNumberSequences() > 0) {
                  a.setNumberSequences(0); // stop animation
                  setSelected(false);
               } else {
                  stepbackwardAction.setEnabled(false);
                  stepforwardAction.setEnabled(false);
                  randomAction.setEnabled(false);
                  setSelected(true);
                  animBox.clearStepsForward();
                  CreateGui.getAnimator().startRandomFiring();
               }
               break;
               
            default:
               break;
         }
      }
      
   }
   
   
   
   class ExampleFileAction extends GuiAction {
      
      private File filename;
      
      
      ExampleFileAction(File file, String keyStroke) {
         super(file.getName(), "Open example file \"" + file.getName() +
                 "\"", keyStroke);
         filename = file;//.getAbsolutePath();
         putValue(SMALL_ICON,
                 new ImageIcon(Thread.currentThread().getContextClassLoader().
                 getResource(CreateGui.imgPath + "Net.png")));
      }
      
      
      ExampleFileAction(JarEntry entry, String keyStroke) {
         super(entry.getName().substring(1 + entry.getName().
                 indexOf(System.getProperty("file.separator"))),
                 "Open example file \"" + entry.getName() + "\"",
                 keyStroke);
         putValue(SMALL_ICON,
                 new ImageIcon(Thread.currentThread().getContextClassLoader().
                 getResource(CreateGui.imgPath + "Net.png")));
         
         filename = JarUtilities.getFile(entry);//.getPath();
      }
      
      
      public void actionPerformed(ActionEvent e){
         createNewTab(filename, false);
      }
      
   }
   
   
   
   class DeleteAction extends GuiAction {
      
      DeleteAction(String name, String tooltip, String keystroke) {
         super(name, tooltip, keystroke);
      }
      
      
      public void actionPerformed(ActionEvent e){
         appView.getUndoManager().newEdit(); // new "transaction""
         appView.getUndoManager().deleteSelection(
                 appView.getSelectionObject().getSelection());
         appView.getSelectionObject().deleteSelection();
      }
      
   }
   
   
   
   class TypeAction extends GuiAction {
      
      private int typeID;
      
      TypeAction(String name, int typeID, String tooltip, String keystroke){
         super(name, tooltip, keystroke);
         this.typeID = typeID;
      }
      
      
      TypeAction(String name, int typeID, String tooltip, String keystroke,
              boolean toggleable){
         super(name, tooltip, keystroke, toggleable);
         this.typeID = typeID;
      }
      
      
      public void actionPerformed(ActionEvent e){
//         if (!isSelected()){
         this.setSelected(true);
         
         // deselect other actions
         if (this != placeAction) {
            placeAction.setSelected(false);
         }
         if (this != transAction) {
            transAction.setSelected(false);
         }
         if (this != timedtransAction) {
            timedtransAction.setSelected(false);
         }
//         if (this != addIntervalTransitionAction){
//        	 addIntervalTransitionAction.setSelected(false);
//         }
         if (this != arcAction) {
            arcAction.setSelected(false);
         }
         if (this != inhibarcAction) {
            inhibarcAction.setSelected(false);
         }
         if (this != tokenAction) {
            tokenAction.setSelected(false);
         }
         if (this != deleteTokenAction) {
            deleteTokenAction.setSelected(false);
         }
         if (this != rateAction) {
            rateAction.setSelected(false);
         }
         if (this != markingAction) {
            markingAction.setSelected(false);
         }
         if (this != selectAction) {
            selectAction.setSelected(false);
         }
         if (this != annotationAction) {
            annotationAction.setSelected(false);
         }
         if (this != dragAction) {
            dragAction.setSelected(false);
         }
         
         if (appView == null){
            return;
         }
         
         appView.getSelectionObject().disableSelection();
         //appView.getSelectionObject().clearSelection();
         
         setMode(typeID);
         statusBar.changeText(typeID);
         
         
         if ((typeID != Pipe.ARC) && (appView.createArc != null)) {
            appView.createArc.delete();
            appView.createArc=null;
            appView.repaint();
         }
         
         if (typeID == Pipe.SELECT) {
            //disable drawing to eliminate possiblity of connecting arc to
            //old coord of moved component
            statusBar.changeText(typeID);
            appView.getSelectionObject().enableSelection();
            appView.setCursorType("arrow");
         } else if (typeID == Pipe.DRAG) {
            appView.setCursorType("move");
         } else {
            appView.setCursorType("crosshair");
         }
      }
//      }
      
   }
   
   
   
   class GridAction extends GuiAction {
      
      GridAction(String name, String tooltip, String keystroke) {
         super(name, tooltip, keystroke);
      }
      
      
      public void actionPerformed(ActionEvent e) {
         Grid.increment();
         repaint();
      }
      
   }
   
   
   
   class ZoomAction extends GuiAction {
      
      ZoomAction(String name, String tooltip, String keystroke) {
         super(name, tooltip, keystroke);
      }
      
      
      public void actionPerformed(ActionEvent e) {
         boolean doZoom = false;
         try {
            String actionName = (String)getValue(NAME);
            Zoomer zoomer = appView.getZoomController();
            JViewport thisView =
                    ((JScrollPane)appTab.getSelectedComponent()).getViewport();
            String selection = null, strToTest = null;
            
            double midpointX = Zoomer.getUnzoomedValue(
                    thisView.getViewPosition().x + (thisView.getWidth() * 0.5),
                    zoomer.getPercent());
            double midpointY = Zoomer.getUnzoomedValue(
                    thisView.getViewPosition().y + (thisView.getHeight() * 0.5),
                    zoomer.getPercent());
            
            if (actionName.equals("Zoom in")){
               doZoom = zoomer.zoomIn();
            } else if (actionName.equals("Zoom out")) {
               doZoom = zoomer.zoomOut();
            } else {
               if (actionName.equals("Zoom")) {
                  selection = (String)zoomComboBox.getSelectedItem();
               }
               if(e.getSource() instanceof JMenuItem){
                  selection = ((JMenuItem)e.getSource()).getText();
               }
               strToTest = validatePercent(selection);
               
               
               if (strToTest!=null) {
                  //BK: no need to zoom if already at that level
                  if (zoomer.getPercent() == Integer.parseInt(strToTest)) {
                     return;
                  } else {
                     zoomer.setZoom(Integer.parseInt(strToTest));
                     doZoom = true;
                  }
               } else {
                  return;
               }
            }
            if (doZoom == true) {
               updateZoomCombo();
               appView.zoomTo(new java.awt.Point((int)midpointX, (int)midpointY));
            }
         } catch (ClassCastException cce) {
            // zoom 
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      
      
      private String validatePercent(String selection) {
         
         try {
            String toTest = selection;
            
            if(selection.endsWith("%")){
               toTest=selection.substring(0, (selection.length())-1);
            }
            
            if (Integer.parseInt(toTest) < Pipe.ZOOM_MIN ||
                    Integer.parseInt(toTest) > Pipe.ZOOM_MAX) {
               throw new Exception();
            } else {
               return toTest;
            }
         } catch(Exception e) {
            zoomComboBox.setSelectedItem("");
            return null;
         }
      }
      
   }
   
   
   
   class ExperimentAction extends GuiAction{
      
      ExperimentAction(String name, String tooltip, String keystroke) {
         super(name, tooltip, keystroke);
      }
      
      public void actionPerformed(ActionEvent e) {
         if (this == loadExperimentAction) {
            File filePath = new FileBrowser(CreateGui.userPath).openFile();
            if ((filePath != null) && filePath.exists()
            && filePath.isFile() && filePath.canRead()) {
               loadExperiment(filePath.getAbsolutePath());
            }
         }
         if (this == experimentEditorAction) {
            ExperimentEditor ee = new ExperimentEditor(appModel);
            //ee.start();
         }
      }
      
   }
   
   
   
   class FileAction extends GuiAction {
      
      //constructor
      FileAction(String name, String tooltip, String keystroke) {
         super(name, tooltip, keystroke);
      }
      
      public void actionPerformed(ActionEvent e) {
         if (this == saveAction) {
            saveOperation(false);            	  // code for Save operation
         } else if(this == saveAsAction) {
            saveOperation(true);                  // code for Save As operations
         } else if (this == openAction) {         // code for Open operation
            File filePath = new FileBrowser(CreateGui.userPath).openFile();
            if ((filePath != null) && filePath.exists()
            && filePath.isFile() && filePath.canRead()) {
               CreateGui.userPath = filePath.getParent();
               createNewTab(filePath, false);
            }
         } else if (this == importAction) {
            File filePath = new FileBrowser(CreateGui.userPath).openFile();
            if ((filePath != null) && filePath.exists() && filePath.isFile() &&
                    filePath.canRead()) {
               CreateGui.userPath = filePath.getParent();
               createNewTab(filePath,true);
               appView.getSelectionObject().enableSelection();
               
            }
         } else if (this == createAction) {
            createNewTab(null, false);            // Create a new tab
         } else if ((this == exitAction) && checkForSaveAll()) {
            dispose();
            System.exit(0);
         } else if ((this == closeAction) && (appTab.getTabCount() > 0)
         && checkForSave()) {
            setObjectsNull(appTab.getSelectedIndex());
            appTab.remove(appTab.getSelectedIndex());
         } else if (this == exportPNGAction) {
            Export.exportGuiView(appView, Export.PNG, null);
         } else if (this == exportPSAction) {
            Export.exportGuiView(appView, Export.POSTSCRIPT, null);
         } else if (this == exportTNAction){
            System.out.println("Exportant a TN");
            Export.exportGuiView(appView, Export.TN, appModel);
         } else if (this == printAction) {
            Export.exportGuiView(appView, Export.PRINTER, null);
         }
      }
      
   }
   
   
 
  
   
   
   
   
   
   class EditAction extends GuiAction {
      
      EditAction(String name, String tooltip, String keystroke) {
         super(name, tooltip, keystroke);
      }
      
      
      public void actionPerformed(ActionEvent e){
         
         if (CreateGui.getApp().isEditionAllowed()) {
            if (this == cutAction) {
               ArrayList selection = appView.getSelectionObject().getSelection();
               appGui.getCopyPasteManager().setUpPaste(selection, appView);
               appView.getUndoManager().newEdit(); // new "transaction""
               appView.getUndoManager().deleteSelection(selection);
               appView.getSelectionObject().deleteSelection();
               pasteAction.setEnabled(appGui.getCopyPasteManager().pasteEnabled());
            } else if (this == copyAction) {
               appGui.getCopyPasteManager().setUpPaste(
                       appView.getSelectionObject().getSelection(), appView);
               pasteAction.setEnabled(appGui.getCopyPasteManager().pasteEnabled());
            } else if (this == pasteAction) {
               appView.getSelectionObject().clearSelection();
               appGui.getCopyPasteManager().startPaste(appView);
            } else if (this == undoAction) {
               appView.getUndoManager().undo();
            } else if (this == redoAction) {
               appView.getUndoManager().redo();
            }
         }
      }
   }
   
   
   
   /**
    * A JToggleButton that watches an Action for selection change
    * @author Maxim
    *
    * Selection must be stored in the action using putValue("selected",Boolean);
    */
   class ToggleButton extends JToggleButton implements PropertyChangeListener {
      
      public ToggleButton(Action a) {
         super(a);
         if (a.getValue(Action.SMALL_ICON) != null) {
            // toggle buttons like to have images *and* text, nasty
            setText(null);
         }
         a.addPropertyChangeListener(this);
      }
      
      
      public void propertyChange(PropertyChangeEvent evt) {
         if (evt.getPropertyName() == "selected") {
            Boolean b = (Boolean)evt.getNewValue();
            if (b != null) {
               setSelected(b.booleanValue());
            }
         }
      }
      
   }
   
   
   
   class WindowHandler extends WindowAdapter{
      //Handler for window closing event
      public void windowClosing(WindowEvent e){
         exitAction.actionPerformed(null);
      }
   }
   
   
   JTable  table=null;
   int composetab=-1;
// du: Ϊ��analysis MRG�˵���
   class MRGAction extends GuiAction {
	   
	      
	      //constructor
	      private int typeID;
	    
	      //���ӵ�MT������
	      private int tpx=300;
	      private int tpy=100;
	      private int tcnt=0;
	      private int tpyy=100;
          //�ϲ�ͼ������
	      private int t1x=50;
	      private int t1y=50;	      
	      private int t2x=400;
	      private int t2y=50;
	      
	      JFrame dialog=null;
	     
	      
	      Object[][] data=null;
	      
	     
	      MRGAction(String name, int typeID, String tooltip, String keystroke,
	              boolean toggleable){
	         super(name, tooltip, keystroke, toggleable);
	         this.typeID = typeID;
	      }
	      
	      private Point adjustPoint(Point p, int zoom) {
	          int offset = (int)(Zoomer.getScaleFactor(zoom) *
	                  Pipe.PLACE_TRANSITION_HEIGHT/2);
	          
	          int x = Zoomer.getUnzoomedValue(p.x - offset, zoom);
	          int y = Zoomer.getUnzoomedValue(p.y - offset, zoom);
	          
	          p.setLocation(x, y);
	          return p;
	       }
	      
	      private PlaceTransitionObject newPlace(Point p,String name){
          p = adjustPoint(p, appView.getZoom());
	          
	          PetriNetObject  pnObject = new Place(Grid.getModifiedX(p.x), Grid.getModifiedY(p.y));
	          pnObject.setName(name);
	          appModel.addPetriNetObject(pnObject);
	          appView.addNewPetriNetObject(pnObject);
	          return (PlaceTransitionObject)pnObject; 
	       }
	      
	      //��Ӱ�ɫ����
	      private PlaceTransitionObject newTimedTransition(Point p, String name){
	          p = adjustPoint(p, appView.getZoom());
	          
	          PetriNetObject pnObject = new Transition(Grid.getModifiedX(p.x),
	                  Grid.getModifiedY(p.y));
	          pnObject.setName(name);
	          ((Transition)pnObject).setTimed(true);
	          appModel.addPetriNetObject(pnObject);
	          appView.addNewPetriNetObject(pnObject);
	          return (PlaceTransitionObject)pnObject; 
	       }
	      
	      private PetriNetObject findObject(String name){
	      
	    	  ArrayList <PetriNetObject> pnObjects = appView.getPNObjects();
	    	  String gn=null;
	    	  for (PetriNetObject pnObject : pnObjects) {	    	        
	    		  gn=pnObject.getName();
	    		  if(gn!=null){
	    		  gn=gn.toUpperCase();	    		  
	    		  }
	    		  if (gn!=null&&gn.equals(name.toUpperCase())){
	    			  return pnObject;
	    		  }	    		  
	    	  }    
	    	  return null;	      
	      }
	      	      	     
	      private void AddMT(String tsname,String toname,String inout,String cl){
	    	  	    	 
	    	  PlaceTransitionObject po=(PlaceTransitionObject)findObject(toname);	    	  
	    	  if(po==null){//���󲻴��ڣ��˳�
	    		  return;
	    	  }
	    	  	    	  	    	  	    	  
	    	  PlaceTransitionObject pt=(PlaceTransitionObject)findObject(tsname);
	    	  if(pt==null){	  //���󲻴������
	    		  pt=newTimedTransition(new Point(tpx,tpy+tcnt*tpyy),tsname);
	    		  tcnt++;
	    	  }
	    	  NormalArc p3=null;
	    	  if(inout.toUpperCase().equals("IN")){
	    		  p3= new  NormalArc(po.getPositionX(), po.getPositionY(), 
	                      pt.getPositionX(),pt.getPositionY(), 
	                      po, 
	                      pt,
	                      1,
	                      null,
	                      false);
	    		  po.addConnectFrom(p3);
	    		  pt.addConnectTo(p3); 
	    		  
	    	  }else{
	    	  
	    		  p3= new  NormalArc(pt.getPositionX(), pt.getPositionY(), 
	                      po.getPositionX(),po.getPositionY(), 
	                      pt, 
	                      po,
	                      1,
	                      null,
	                      false);
	    		  pt.addConnectFrom(p3);
	    		  po.addConnectTo(p3); 
	    		  
	    	  }
	    	  
	    	 
    		  appModel.addPetriNetObject(p3);
	          appView.addNewPetriNetObject(p3);
	    	  
	    	  /*createNewTab(null, false);
    		  PlaceTransitionObject p1=  newPlace(new Point(50,100),"P110");
    		  PlaceTransitionObject p2=newTimedTransition(new Point(200,100),"T110");
    		  
    		  
    		  NormalArc p3= new  NormalArc(50, 100, 
                      200, 100, 
                      p1, 
                      p2,
                      1,
                      "A1",
                      true);
    		  
    		  
    		  p1.addConnectFrom(p3);
    		  p2.addConnectTo(p3);
    		  appModel.addPetriNetObject(p3);
	          appView.addNewPetriNetObject(p3);*/
	    	  return;
	      }
	      
	     
	      private void AddMTs(){
	    	  
	    	  tcnt=0;
	    	  DefaultTableModel tableModel = (DefaultTableModel) table.getModel();	  
			  String tsname=null;
			  String toname=null;
			  String inout=null;
			  String cl=null;
			  for(int i =0;i< table.getRowCount();i++){
				  tsname=(String) tableModel.getValueAt(i, 0);
				  toname=(String) tableModel.getValueAt(i, 1);
				  inout=(String) tableModel.getValueAt(i, 2);
				  cl=(String) tableModel.getValueAt(i, 3);	
				  
				  AddMT(tsname,toname,inout,cl);
			  }
	      }
	      
	      private void copyTab(int from , int to, int x, int y){
	    	  	    	 
	    	  appTab.setSelectedIndex(from);	    			    				 
    		  ArrayList <PetriNetObject> pnObjects1 = appView.getPNObjects();	    		  
    	      appGui.getCopyPasteManager().setUpPaste(pnObjects1,appView);	    	      	    		    	    
              pasteAction.setEnabled(appGui.getCopyPasteManager().pasteEnabled());
               	 
              
              if(to==-1){
            	  createNewTab(null,false);
            	  composetab =appTab.getSelectedIndex(); //û�����ô���
	    	  }else{
	    		  appTab.setSelectedIndex(to);	    
	    	  }
              
              appView.getSelectionObject().clearSelection();
              appGui.getCopyPasteManager().endPaste2(appView,x,y);	 
	      }
	      
	      //����  �˴�����ܹ���ʼ��Ϊ�ձ�(��ʱ���)
		private void showAddMTs() {
			dialog = new JFrame("Add MTs");
			Container contentPane = dialog.getContentPane();
			contentPane.setLayout(new BorderLayout(5, 5));
			String[] columnNames = { "tsname", "toname", "in/out", "class" };
			data = new Object[][] {

//			 {"t7", "p5","in","aa"},
//			 {"t7", "p14","out","bb"},
//			 {"t8", "p6","out","cc"},
//			 {"t8", "p9","out","cc"},
//			 {"t8", "p15","in","dd"},
//			 {"t9", "p7","in","ee"},
//			 {"t9", "p8","in","ff"},
//			 {"t9", "p16","out","gg"},
			};

			DefaultTableModel model = new DefaultTableModel(data, columnNames);

			table = new JTable(model);
    	      
    	      JScrollPane scroller=new JScrollPane(table);
    	      scroller.setBorder(new BevelBorder(BevelBorder.LOWERED));
    	         
    	      dialog.setIconImage(((ImageIcon)this.getValue(SMALL_ICON)).getImage());
    	         
    	      scroller.setPreferredSize(new Dimension(400,400));
    	      contentPane.add(scroller,BorderLayout.CENTER);
    	       
    	      contentPane.add(new ButtonBar(new String[]{"ADD","DELETE","SAVE","CLOSE"},
    	        new ActionListener[] {this,this,this,this}),BorderLayout.PAGE_START);
    	      
    	      dialog.pack(); 
    		  
    	      dialog.setLocationRelativeTo(CreateGui.getApp());
    	      dialog.setVisible(true);  
	      }
	      
	      
	      private PIPENode find(ArrayList nodes, String name){
	    	  
	    	  String tmp="";
	    	  for (Object pnObject : nodes) {	    	        	    		  
	    		  tmp=((PIPENode)pnObject).getMarking();
	    		  if(tmp.indexOf("-"+name)>=0){
	    			  return (PIPENode)pnObject;
	    		  }	    		 
	    	  }    	    	 
	        return null;
	      }
	      
	      private void ShowFrame1(){
	    	  DefaultGraph graph = new DefaultGraph();
    		  ArrayList nodes = new ArrayList();
    	      ArrayList edges = new ArrayList();
    	      
    	      nodes.add(new PIPEVanishingState("p1","RG1-1"));	    	      	    	    	      
    	      nodes.add(new PIPEVanishingState("p2 p5","RG1-2")); 	      
    	      nodes.add(new PIPEVanishingState("p2","RG1-3"));	    	      
    	      nodes.add(new PIPEVanishingState("p2 p6 p8","RG1-4"));
    	      nodes.add(new PIPEVanishingState("p3 p7 p8","RG1-5"));
    	      nodes.add(new PIPEVanishingState("p4 p7 p9","RG1-6"));
    	      
    	      edges.add(new TextEdge(find(nodes,"1"),find(nodes,"2"),"t1"));
    	      edges.add(new TextEdge(find(nodes,"4"),find(nodes,"5"),"t2"));
    	      edges.add(new TextEdge(find(nodes,"5"),find(nodes,"6"),"t3"));
    	      
    	      graph.addElements(nodes, edges);
    	      
    	      
    	      
    	      GraphFrame frame1 = new GraphFrame();
    	      String legend ="Test";
    	      
    	      frame1.constructGraphFrame(graph, legend);
    	      
    	      frame1.setSize(400, 300);
    	      frame1.setLocation(100, 100);
    	      
    	      
    	      frame1.toFront();
    	      frame1.setIconImage((
    	              new ImageIcon(Thread.currentThread().getContextClassLoader().
    	              getResource(CreateGui.imgPath + "icon.png")).getImage()));
    	      frame1.setTitle("eBay");
	      }
	      
	      private void ShowFrame2(){
	    	  DefaultGraph graph = new DefaultGraph();
    		  ArrayList nodes = new ArrayList();
    	      ArrayList edges = new ArrayList();
    	      
       	      
              nodes.add(new PIPEVanishingState("p10","RG2-1"));	    	      	    	    	      
    	      nodes.add(new PIPEVanishingState("p10 p14","RG2-2")); 	      
    	      nodes.add(new PIPEVanishingState("p11","RG2-3"));	
    	      nodes.add(new PIPEVanishingState("p12 p15","RG2-4"));	
    	      nodes.add(new PIPEVanishingState("p12","RG2-5"));
    	      nodes.add(new PIPEVanishingState("p12 p16","RG2-6"));
    	      nodes.add(new PIPEVanishingState("p13 p17","RG2-7"));
    	      
    	      edges.add(new TextEdge(find(nodes,"2"),find(nodes,"3"),"t4"));
    	      edges.add(new TextEdge(find(nodes,"3"),find(nodes,"4"),"t5"));
    	      edges.add(new TextEdge(find(nodes,"6"),find(nodes,"7"),"t6"));
    	      
    	     
    	      
    	      
    	      graph.addElements(nodes, edges);
    	      
    	      
    	      GraphFrame frame1 = new GraphFrame();
    	      
    	      
    	      
    	      String legend ="Test";
    	      
    	      frame1.constructGraphFrame(graph, legend);
    	      
    	      frame1.setSize(400, 300);
    	      frame1.setLocation(500, 100);
    	      
    	      frame1.toFront();
    	      frame1.setIconImage((
    	              new ImageIcon(Thread.currentThread().getContextClassLoader().
    	              getResource(CreateGui.imgPath + "icon.png")).getImage()));
    	      frame1.setTitle("TPC");
	      }
	      
	      //���� ������
		private void ShowFrame4() {
			JFrame guiDialog = new JFrame("Add limit");

			guiDialog.setSize(400, 100);
			guiDialog.setLocation(500, 300);

			guiDialog.setVisible(true);

			// java.awt.GridBagConstraints gridBagConstraints;
			limitPanel = new javax.swing.JPanel();
			limitLabel = new javax.swing.JLabel("����������ʱ�䣺");
			limitPanel.add(limitLabel);

			limitTextField = new javax.swing.JTextField(null, 10);
			limitPanel.add(limitTextField);
			Button button = new Button("����");

			button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					buttonHandler(evt);
				}
			});

			limitPanel.add(button);
			guiDialog.getContentPane().add(limitPanel);

			button.addActionListener(limitAction);

		}
	      private void buttonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_okButtonKeyPressed
    	      if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
    	         buttonHandler(new java.awt.event.ActionEvent(this,0,""));
    	      }
    	   }
    	  
    	  private void buttonHandler(java.awt.event.ActionEvent evt) {
    		  setLimittime(Double.parseDouble(limitTextField.getText()));
    	  }

	      private void ShowFrame3(){
	    	  
	    	  DefaultGraph graph = new DefaultGraph();
	    	  
    		  ArrayList nodes = new ArrayList();
    	      ArrayList edges = new ArrayList();

    	      nodes.add(new PIPEState("p1p10","SG-1"));	    	      	    	    	      
    	      nodes.add(new PIPEState("p2p10 p14","SG-2")); 	      
    	      nodes.add(new PIPEState("p2p6 p8 p12","SG-3"));	    	      
    	      nodes.add(new PIPEState("p4p12 p16","SG-4"));
    	     
    	      
    	      edges.add(new TextEdge(find(nodes,"1"),find(nodes,"2"),"(p2p5p10, t7,p2p10p14)"));
    	      edges.add(new TextEdge(find(nodes,"2"),find(nodes,"3"),"(p2p12p15, t8, p2p6p8p12)"));
    	      edges.add(new TextEdge(find(nodes,"3"),find(nodes,"4"),"(p4p7p9p12, t9, p4p12p16)"));
    	      
    	      graph.addElements(nodes, edges);
    	      
    	      
    	      GraphFrame frame1 = new GraphFrame();
    	      String legend ="Test";
    	      
    	      frame1.constructGraphFrame(graph, legend);
    	      frame1.setSize(400, 300);
    	      frame1.setLocation(100, 400);
    	      
    	      frame1.toFront();
    	      frame1.setIconImage((
    	              new ImageIcon(Thread.currentThread().getContextClassLoader().
    	              getResource(CreateGui.imgPath + "icon.png")).getImage()));
    	      frame1.setTitle("SG");
	      }
	      
	      
	      private void ShowResult(){
	    	  
	    	  JFrame guiDialog = new JFrame("MTs Analysis");
	    	  
	    	  Container contentPane = guiDialog.getContentPane();
	    	  

	        // 3 Add results pane
	    	  ResultsMTsPane  results = new ResultsMTsPane("Results");
	          contentPane.add(results);

	          results.setText("����������������");
	          guiDialog.setSize(400, 300);
	          guiDialog.setLocation(500, 400);
	          // 5 Make window fit contents' preferred size
	         // guiDialog.pack();


	          guiDialog.setVisible(true);
	    	  
	    	  
	      }
	      
	      //���� 
	    //�½�һ������������������ArrayList�����ݸ���
	      private ArrayList copyary(ArrayList a){
	   	   int i = a.size();
	   	   ArrayList b = new ArrayList();
	   	   for (int k=0;k<i;k++){
	   	 	 b.add(k, a.get(k));
	   	 	 }
	   	   return b;
	   	   }
	      
	 
	      
	      //���ж���·����ʱ���ж�
		java.util.ArrayList nd = ReachabilityGraphGenerator.nodes1;
		java.util.ArrayList eg = ReachabilityGraphGenerator.edges1;
		public java.util.ArrayList<List> arraynode = new ArrayList<List>();
		public java.util.ArrayList<List> arrayedge = new ArrayList<List>();
		public List<Edge> edgesTo=null;
		public List<Node> node;

		// numAry��arraynode��arrayedge��size
		public int numAry = 0;
		public int numNd = 0;
		// ������ʾ���ַ���
		
		public void multPath() {

			// arraynode.add(0, null);
			ArrayList edges2 = new ArrayList();
			arrayedge.add(0, edges2);
			// arraynode.get(numAry).add(0, nd.get(0));
			ArrayList array = new ArrayList();
			array.add(nd.get(0));
			arraynode.add(0, array);
			for (int n = 0; n < arraynode.size(); n++) {
				// (arraynode.get(n).get(numNd))).getEdgesFrom().size());
				while (((DefaultNode) (arraynode.get(n)).get(arraynode.get(n)
						.size() - 1)).getEdgesFrom().size() != 0) {
					edgesTo = ((Node) (arraynode.get(n)).get(arraynode.get(n)
							.size() - 1)).getEdgesFrom();
					// (arraynode.get(n).get(numNd))).getEdgesFrom().size());
					if (edgesTo.size() > 1) {
						int i = edgesTo.size();

						// ����i����i-1�����飬�������鸳ֵ
						for (int k = 1; k < i; k++) {
							numAry = numAry + 1;

							// ����з�֧����arraynode���½�һ��List����ʹ�½���list����ԭlist
							// Ȼ�󣬽���֧��Ľڵ�ֱ���뵽�⼸����list��

							ArrayList ary = new ArrayList();
							ary = copyary((ArrayList) arraynode.get(n));
							arraynode.add(numAry, ary);

							arraynode.get(numAry).add(
									((DefaultEdge) edgesTo.get(k)).getTo());

							// ͬ�����������edge��
							ArrayList ary1 = new ArrayList();
							ary1 = copyary((ArrayList) arrayedge
									.get(numAry - 1));
							arrayedge.add(numAry, ary1);
							arrayedge.get(numAry).add(
									(DefaultEdge) edgesTo.get(k));
						}
						// ����֧��Ľڵ���뵽ԭlist��
						((List) arrayedge.get(n)).add((DefaultEdge) edgesTo
								.get(0));
						((List) arraynode.get(n))
								.add((DefaultNode) ((DefaultEdge) edgesTo
										.get(0)).getTo());
					} else if (edgesTo.size() == 1) {
						// ���û�з�֧�Ļ���ֱ����ӵ�list��
						((List) arrayedge.get(n)).add((DefaultEdge) edgesTo
								.get(0));
						((List) arraynode.get(n))
								.add((DefaultNode) ((DefaultEdge) edgesTo
										.get(0)).getTo());
					} 

				}
			}

			for (int i = 0; i < arrayedge.size(); i++) {
				double startsum = 0;
				double endsum=0;
				str += "\r\n"+" \r\n�������̣�"+"[";
				for (int k = 0; k < arrayedge.get(i).size(); k++) {
					str += ((TextEdge) arrayedge.get(i).get(k)).getText();
					if(k!=arrayedge.get(i).size()){
						str+="-->";
					}	
				}
				str+="]";
				
				
	
		        
				for (int k = 0; k < arrayedge.get(i).size(); k++) {
					for (int j = 0; j < DataLayer.getTransitionSize(); j++) {
						if (DataLayer.getTransitionName(j) == ((TextEdge) arrayedge
								.get(i).get(k)).getText()) {
							startsum += DataLayer.getTransitionStarttime(j);
							endsum += DataLayer.getTransitionEndtime(j);
						}
					}

				}
				str += "\r\n"+"����������·������ʱ��Ϊ��" + "["+startsum+","+endsum+"]";
	    		  if (limittime > endsum){
	    			  str += "\r\n�������޶�ʱ��֮�����,��ѡ������";
	    		  }
	    		  else if(limittime < startsum){
	    			  str += "\r\n���񽫲������޶�ʱ��֮����ɣ���Ľ�����ѡ����������·��";
	    		  }
	    		  else {
	    			  str += "\r\n����������޶�ʱ������ɣ�Ҳ�������޶�ʱ��֮����ɣ�����ѡ�񣬵����иĽ��ı�Ҫ";
				}
	    	  
				}	
			sernum = arrayedge.size();
		}
	      
		public String Result2() {
			String str1="";
//			for (int i=0;i<sernum;i++) {
//				str1 += getTransitionName((ArrayList)multPath().get(i));
//			}
			return "����" + sernum + "���������            " + "\r\n������" + str ;

		}
		
	      
	      private void ShowResult1(){
	    	  
	    	  JFrame guiDialog = new JFrame("time analysis");
	    	  
	    	  Container contentPane = guiDialog.getContentPane();
	    	  

	        // 3 Add results pane
	    	  ResultsMTsPane  results = new ResultsMTsPane("Results");
	          contentPane.add(results);

	          //results.setText();
	          

	          
	          //���� ��ʾ���
	          results.setText( Result2());
	          
	          
	          guiDialog.setSize(400, 400);
	          guiDialog.setLocation(500, 400);
	          // 5 Make window fit contents' preferred size
	         // guiDialog.pack();


	          guiDialog.setVisible(true);
	    	  
	    	  
	      }
	      
	      
	      public void actionPerformed(ActionEvent e) {
	       

	    	  this.setSelected(true);
	      	  
	    	  if(this==DecomposeAction){	    		  
	    		  //��һ��ͼ	    		               
	              copyTab(0,-1,t1x,t1y);
	              AddMTs();
	              saveNet(new File("./DecomposePart1.xml"));
	              //�ڶ���ͼ	             
	              copyTab(1,-1,t2x,t2y);
	              AddMTs();	              	            
	              saveNet(new File("./DecomposePart2.xml"));
	              return;
	              
	    	  }
	    	  
	    	  if(this==ConstructAction){
	    		  
	    		  ShowFrame1();
	    	      ShowFrame2();
	    		  ShowFrame3();
	    		  ShowResult();
	    	  }
	    	  
	    	  if(this==Analysis1Action){
	    		  multPath();
	    		  Result2();		  
	    		  ShowResult1();	    		  
	    	  }
	    	  if(this == limitFrame){
	    		  ShowFrame4(); 
	    	  }
	    	     	  
	    	  if(this==MTAction){
	    		 
	    		  String s = e.getActionCommand();
	    		  
	    		  if (s=="CLOSE"){
	    			  dialog.setVisible(false);
	    			  return;
	    		  }else if(s=="ADD"){
	    			  DefaultTableModel  tableModel = (DefaultTableModel ) table.getModel();
	    			  tableModel.addRow(new Object[]{"", "", "",""});
	    			  return;
	    		  }else if(s=="DELETE"){
	    			  DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
	    			  
	    			  if(table.getSelectedRowCount()==1){	    			  
	    				  tableModel.removeRow(table.getSelectedRow());
	    			  }
	    			  return;
	    		  }else if(s=="SAVE"){	  
	    		      //�½��ļ�	    			  	    			  
	    			  
	    			  copyTab(composetab,-1,50,50);
	    			  
	    			  AddMTs(); 
	    			  saveNet(new File("./compose2.xml"));
	    			  
	    			  dialog.setVisible(false); 
	    			  return;
	    			  
	    		  }
	    		  
	    		  showAddMTs();
	    		 
	    	  }
	    	  
	    	  
	    	  if (this == ComposeAction) {
	    		 	    	
	    		  
	    		  if(CreateGui.getTabCount()<2){
	    			  return;
	    		  }
	    		  	
	    		  composetab=-1;
	    		  copyTab(0,composetab,t1x,t1y);	
	    		  
	    		  copyTab(1,composetab,t2x,t2y);	
	    		  
	 

                  // д�ļ�	    	
	              saveNet(new File("./Compose1.xml"));
	          
	    		  
	    		/*
	              setObjectsNull(appTab.getSelectedIndex());
	              appTab.remove(appTab.getSelectedIndex());
	              
	              CreateGui.userPath = filePath.getParent();
	              createNewTab(filePath, false);*/
	              
	              ComposeAction.setSelected(false);
	    	      
	    	      
	    	  }	    	    
	    	  
	   
     	   }
	      
   }
   //du��������
   	  java.awt.GridBagConstraints gridBagConstraints;
   	  private javax.swing.JLabel limitLabel;
	  private javax.swing.JTextField limitTextField;
	  
	  public javax.swing.JTextField getLimitTextField() 
	  {
		return limitTextField;
	  }


	private javax.swing.JPanel limitPanel;
	  
   
   
}
