package net.sourceforge.jpowergraph;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Action;

import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;

/**
 * @author Mick Kerrigan
 *
 * Created on 07-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/common/src/net/sourceforge/jpowergraph/Legend.java,v $,
 * @version $Revision: 1.2 $ $Date: 2007/05/20 17:47:20 $
 */

public class Legend {

    private GroupLegendItem root;
    private JPowerGraphRectangle location;
    private HashMap <JPowerGraphRectangle, Action> rectangleActionMap;
    private boolean interactive = false;
    
    public Legend(GroupLegendItem theRoot, boolean isInteractive) {
        root = theRoot;
        if (root == null){
            root = new GroupLegendItem();
        }
        rectangleActionMap = new HashMap <JPowerGraphRectangle, Action> ();
        location = new JPowerGraphRectangle(0, 0, 0, 0);
        interactive  = isInteractive;
    }
    
    public JPowerGraphRectangle getLocation(){
        return location;
    }
    
    public void setLocation(JPowerGraphRectangle theLocation) {
        this.location = theLocation;
    }

    public void clear() {
        root.clear();
    }
    
    public GroupLegendItem getRoot(){
        return root;
    }

    public void clearActionMap(){
        rectangleActionMap.clear();
    }
    
    public void addActionRectangle(JPowerGraphRectangle theRectangle, Action theAction){
        rectangleActionMap.put(theRectangle, theAction);
    }
    
    public Action getActionAtPoint(JPowerGraphPoint point) {
        for (Iterator i = rectangleActionMap.keySet().iterator(); i.hasNext();) {
            JPowerGraphRectangle rectangle = (JPowerGraphRectangle) i.next();
            if (rectangle.contains(point)){
                return rectangleActionMap.get(rectangle);
            }
        }
        return null;
    }

    public void add(NodeLegendItem item) {
        root.add(item);
    }

    public boolean isInteractive() {
        return interactive;
    }
}