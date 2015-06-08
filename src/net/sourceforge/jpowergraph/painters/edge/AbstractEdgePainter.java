package net.sourceforge.jpowergraph.painters.edge;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.painters.EdgePainter;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;

/**
 * The abstract painter for edges.
 */
public abstract class AbstractEdgePainter <T extends Edge> implements EdgePainter <T> {
    /**
     * Returns the distance of the point to the edge.
     *
     * @param graphPane             the graph pane
     * @param edge                  the edge
     * @param point                 the point
     * @return                      the distance of the point from the edge
     */
    public double screenDistanceFromEdge(JGraphPane graphPane, T edge, JPowerGraphPoint point) {
        double px=point.x;
        double py=point.y;
        JPowerGraphPoint from = graphPane.getScreenPointForNode(edge.getFrom());
        JPowerGraphPoint to = graphPane.getScreenPointForNode(edge.getTo());
        double x1=from.x;
        double y1=from.y;
        double x2=to.x;
        double y2=to.y;
        if (px<Math.min(x1,x2)-8 || px>Math.max(x1,x2)+8 || py<Math.min(y1,y2)-8 || py>Math.max(y1,y2)+8)
            return 1000;
        double dist=1000;
        if (x1-x2!=0)
            dist=Math.abs((y2-y1)/(x2-x1)*(px-x1)+(y1-py));
        if (y1-y2!=0)
            dist=Math.min(dist,Math.abs((x2-x1)/(y2-y1)*(py-y1)+(x1-px)));
        return dist;
    }
    /**
     * Returns the outer rectangle of the edge on screen.
     *
     * @param graphPane             the graph pane
     * @param edge                  the edge
     * @param edgeScreenRectangle   the rectangle receiving the edge's coordinates
     */
    public void getEdgeScreenBounds(JGraphPane graphPane, T edge, JPowerGraphRectangle edgeScreenRectangle) {
        JPowerGraphPoint from=graphPane.getScreenPointForNode(edge.getFrom());
        JPowerGraphPoint to=graphPane.getScreenPointForNode(edge.getTo());
        
        edgeScreenRectangle.x = Math.min(from.x,to.x);
        edgeScreenRectangle.y = Math.min(from.y,to.y);
        edgeScreenRectangle.width = Math.abs(to.x-from.x)+1;
        edgeScreenRectangle.height = Math.abs(to.y-from.y)+1;
    }
}
