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
import net.sourceforge.jpowergraph.manipulator.dragging.DraggingManipulator;
import net.sourceforge.jpowergraph.manipulator.selection.HighlightingManipulator;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;


public class ArrowEdgePainter <T extends Edge> extends AbstractEdgePainter <T> {

protected static final double ARROW_BASE_LENGTH=3.0;
    
    private JPowerGraphColor notHighlighted;
    private JPowerGraphColor normal;
    private JPowerGraphColor highlighted;
    
    public ArrowEdgePainter (){
        notHighlighted = new JPowerGraphColor(197, 197, 197);
        normal = JPowerGraphColor.GRAY;
        highlighted = JPowerGraphColor.RED;
    }
    
    public void paintEdge(JGraphPane graphPane, JPowerGraphGraphics g, T edge, SubGraphHighlighter theSubGraphHighlighter) {
        HighlightingManipulator highlightingManipulator=(HighlightingManipulator) graphPane.getManipulator(HighlightingManipulator.NAME);
        boolean isHighlighted=highlightingManipulator!=null && highlightingManipulator.getHighlightedEdge()==edge;
        DraggingManipulator draggingManipulator=(DraggingManipulator) graphPane.getManipulator(DraggingManipulator.NAME);
        boolean isDragging=draggingManipulator!=null && draggingManipulator.getDraggedEdge()==edge;
        JPowerGraphPoint from=graphPane.getScreenPointForNode(edge.getFrom());
        JPowerGraphPoint to=graphPane.getScreenPointForNode(edge.getTo());
        JPowerGraphColor color=g.getBackground();
        g.setBackground(getEdgeColor(edge,isHighlighted,isDragging, theSubGraphHighlighter));
        paintArrow(g,from.x,from.y,to.x,to.y);
        g.setBackground(color);
    }
    
    protected JPowerGraphColor getEdgeColor(Edge edge,boolean isHighlighted,boolean isDragging, SubGraphHighlighter theSubGraphHighlighter) {
        boolean notHighlightedBecauseOfSubGraph = theSubGraphHighlighter.isHighlightSubGraphs() && !theSubGraphHighlighter.doesSubGraphContain(edge);
        if (notHighlightedBecauseOfSubGraph){
            return notHighlighted;
        }
        if (isHighlighted || isDragging){
            return highlighted;
        }
        return normal;
    }
    
    public static void paintArrow(JPowerGraphGraphics g,int x1,int y1,int x2,int y2) {
        double dx;
        double dy;
        double deltaX=x1-x2;
        double deltaY=y1-y2;
        if (Math.abs(deltaY)>Math.abs(deltaX)) {
            double slope=Math.abs(deltaX/deltaY);
            dx=ARROW_BASE_LENGTH/Math.sqrt(1+slope*slope);
            dy=dx*slope;
        }
        else {
            double slope=Math.abs(deltaY/deltaX);
            dy=ARROW_BASE_LENGTH/Math.sqrt(1+slope*slope);
            dx=dy*slope;
        }
        if (deltaY>0)
            dx*=-1;
        if (deltaX<0)
            dy*=-1;
        
        int polyX1 = x2;
        int polyY1 = y2;
        int polyX2 = (int)(x1-dx);
        int polyY2 = (int)(y1-dy);
        int polyX3 = (int)(x1+dx);
        int polyY3 = (int)(y1+dy);
        
        g.fillPolygon(new int[]{polyX1, polyY1, polyX2, polyY2, polyX3, polyY3});
    }
}
