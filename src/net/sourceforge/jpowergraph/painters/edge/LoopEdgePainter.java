/*
 * Copyright (C) 2006 Digital Enterprise Research Insitute (DERI) Innsbruck
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.sourceforge.jpowergraph.painters.edge;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.SubGraphHighlighter;
import net.sourceforge.jpowergraph.defaults.LoopEdge;
import net.sourceforge.jpowergraph.manipulator.dragging.DraggingManipulator;
import net.sourceforge.jpowergraph.manipulator.selection.HighlightingManipulator;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;


public class LoopEdgePainter <T extends LoopEdge> extends AbstractEdgePainter <T> {
    /** Square root of 3 over 6. */
    protected static final double SQUARE_ROOT_OF_3_OVER_2=0.866;
    /** The width of the base of the arrow. */
    protected static final double ARROW_BASE_LENGTH=11.0;
    public static final int CIRCULAR = 0;
    public static final int RECTANGULAR = 1;

    private JPowerGraphColor edgeColor1;
    private JPowerGraphColor edgeColor2;
    private JPowerGraphColor edgeColor3;
    private int shape = CIRCULAR;
    
    private int widthpad = 15;
    private int heightpad = 15;
    
    public LoopEdgePainter(){
        this(JPowerGraphColor.RED, JPowerGraphColor.YELLOW, CIRCULAR);
    }
    
    public LoopEdgePainter(JPowerGraphColor dragging, JPowerGraphColor normal, int theShape){
        this(new JPowerGraphColor(197, 197, 197), dragging, normal, theShape);
    }
    
    public LoopEdgePainter(JPowerGraphColor blackAndWhite, JPowerGraphColor dragging, JPowerGraphColor normal, int theShape){
        edgeColor1 = blackAndWhite;
        edgeColor2 = dragging;
        edgeColor3 = normal;
        shape = theShape;
    }
    
    public  void  paintEdge(JGraphPane graphPane, JPowerGraphGraphics g, T edge, SubGraphHighlighter theSubGraphHighlighter) {
        JPowerGraphRectangle r = new JPowerGraphRectangle(0, 0, 0, 0);
        getEdgeScreenBounds(graphPane, edge, r);

        JPowerGraphColor oldFGColor = g.getForeground();
        JPowerGraphColor oldBGColor = g.getBackground();        
        g.setForeground(getEdgeColor(edge,graphPane, false, theSubGraphHighlighter));
        g.setBackground(getEdgeColor(edge,graphPane, false, theSubGraphHighlighter));
        paintArrow(g, r.x, r.y, r.width, r.height, shape);
        g.setForeground(oldFGColor);
        g.setBackground(oldBGColor);
    }
    
    public static void paintArrow(JPowerGraphGraphics g, int x1, int y1, int width, int height, int theShape) {
        if (theShape == CIRCULAR){
            g.drawOval(x1, y1, width, height);
        }
        else if (theShape == RECTANGULAR){
            g.drawRectangle(x1, y1, width, height);
        }
        int tx = x1 + width/2 - 2;
        int ty = y1 + 1;
        g.fillPolygon(new int[]{tx, ty - 5, tx + 7, ty, tx, ty + 5});
    }
    
    protected JPowerGraphColor getEdgeColor(Edge edge, JGraphPane graphPane, boolean isShowBlackAndWhite, SubGraphHighlighter theSubGraphHighlighter) {
        HighlightingManipulator highlightingManipulator = (HighlightingManipulator)graphPane.getManipulator(HighlightingManipulator.NAME);
        boolean isHighlighted = highlightingManipulator != null && highlightingManipulator.getHighlightedEdge() == edge;
        DraggingManipulator draggingManipulator = (DraggingManipulator)graphPane.getManipulator(DraggingManipulator.NAME);
        boolean isDragging = draggingManipulator != null && draggingManipulator.getDraggedEdge() == edge;
        
        boolean notHighlightedBecauseOfSubGraph = theSubGraphHighlighter.isHighlightSubGraphs() && !theSubGraphHighlighter.doesSubGraphContain(edge);
        if (isShowBlackAndWhite || notHighlightedBecauseOfSubGraph)
            return edgeColor1;
        else if (isHighlighted || isDragging)
            return edgeColor2;
        else {
            return edgeColor3;
        }
    }
    
    public double screenDistanceFromEdge(JGraphPane graphPane, T edge, JPowerGraphPoint point) {
        int px=point.x;
        int py=point.y;
        JPowerGraphRectangle r = new JPowerGraphRectangle(0, 0, 0, 0);
        getEdgeScreenBounds(graphPane, edge, r);
        
        int x1=r.x;
        int y1=r.y + 1;
        int x2=x1 + r.width;
        int y2=y1;
        int x3=x1;
        int y3=y1 + r.height;
        
        JPowerGraphRectangle r1 = new JPowerGraphRectangle(x1 - 6, y1 - 6, 16 + (x2 - x1), 16);
        JPowerGraphRectangle r2 = new JPowerGraphRectangle(x1 - 6, y1 - 6, 16, 16 + (y3 - y1));
        
        double dist=1000;
        if (r1.contains(point)){
            if (x1 <= px && px <= x2){
                dist = Math.abs(y1 - py);
            }
            else if (px < x1){
                dist = Math.abs(x1 - px) + Math.abs(y1 - py);
            }
            else if (px > x2){
                dist = Math.abs(x2 - px) + Math.abs(y2 - py);
            }
        }
        else if (r2.contains(point)){
            if (y1 <= py && py <= y3){
                dist = Math.abs(x1 - px);
            }
            else if (py < y1){
                dist = Math.abs(x1 - px) + Math.abs(y1 - py);
            }
            else if (py > y3){
                dist = Math.abs(x3 - px) + Math.abs(y3 - py);
            }
        }
        return dist/2;
    }
    
    public void getEdgeScreenBounds(JGraphPane graphPane, T edge, JPowerGraphRectangle edgeScreenRectangle) {
        JPowerGraphPoint node = graphPane.getScreenPointForNode(edge.getFrom());
        JPowerGraphRectangle rectangle = new  JPowerGraphRectangle(0, 0, 0, 0);
        graphPane.getNodeScreenBounds(edge.getFrom(), rectangle);
        
        edgeScreenRectangle.width = rectangle.width/2 + Math.max(widthpad, rectangle.width/10);
        edgeScreenRectangle.height = rectangle.height/2 + Math.max(heightpad, rectangle.height/10);
        edgeScreenRectangle.x = node.x - edgeScreenRectangle.width + 2;
        edgeScreenRectangle.y = node.y - edgeScreenRectangle.height + 2;
    }
}
