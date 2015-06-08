package jpowergraph;

import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import net.sourceforge.jpowergraph.defaults.DefaultNode;
import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;

public class SGNode 
              extends DefaultNode{
	   
	   // the state id, used in the graph's legend
	   private String label = "";
	   // the node id, used in the graph's legend
	   private String placeNo = "";
	   
	   private String timeset ="";
	   
	   private String pathset ="";
	   //-- light  red
	   static JPowerGraphColor fgColor = JPowerGraphColor.GREEN;
	   //--black
	   static JPowerGraphColor bgColor = JPowerGraphColor.BLACK;
	   private static ShapeNodePainter shapeNodePainter = new ShapeNodePainter(
	           ShapeNodePainter.ELLIPSE, fgColor, JPowerGraphColor.LIGHT_GRAY,
	           bgColor);  
	   
	   /*
	   // gray
	    private static JPowerGraphColor bgColor = new JPowerGraphColor(128, 128, 128);
	   // black
	    protected static JPowerGraphColor fgColor = new JPowerGraphColor(0, 0, 0);
	   
	   // the ShapeNodePainter for this node
	   private static ShapeNodePainter shapeNodePainter = new ShapeNodePainter(
	           ShapeNodePainter.ELLIPSE, bgColor, bgColor, fgColor);
	   */
	   /**
	    * Creates a new node instance.
	    * @param _label    the node id.
	    * @param _marking  the marking
	    */
	   
	   public SGNode( String _label,String _placeNo,String _pathset ,String _timeset){
		   // used for no
	      this.label = _label;   
	       // used for placeNo
	      this.placeNo = _placeNo;
	      //used for timeset showing
	      this.timeset = _timeset;
	      //used for pathset showing
	      this.pathset = _pathset;
	      
	   }
	   
	   
	   public String getLabel() {
	      return label;
	   }
	   /*
	   public String getLabel2(){
		   return label;
	   }
	   */
	   
	   public String getNodeType(){
	      return "SGNode";
	   }
	   
	   
	   public String getPlaceNo(){
	      return placeNo;
	   }
	   
	   
	   public static ShapeNodePainter getShapeNodePainter(){
	      return shapeNodePainter;
	   }
	   
	   public String getTimesetString(){
		   return timeset;
		   
	   }
	   public String getPathsetString(){
		   return pathset;
	   }


}
