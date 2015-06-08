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


public class LineEdgePainter <T extends Edge> extends AbstractEdgePainter <T> {

    /** Square root of 3 over 6. */
    protected static final double SQUARE_ROOT_OF_3_OVER_2=0.866;
    /** The width of the base of the arrow. */
    protected static final double ARROW_BASE_LENGTH=11.0;

    private JPowerGraphColor edgeColor1;
    private JPowerGraphColor edgeColor2;
    private JPowerGraphColor edgeColor3;
    private boolean dashedLine;
    
    public LineEdgePainter(){
        this(JPowerGraphColor.RED, JPowerGraphColor.GREEN, false);
    }
    
    public LineEdgePainter(JPowerGraphColor dragging, JPowerGraphColor normal, boolean isDashedLine){
        this(new JPowerGraphColor(197, 197, 197), dragging, normal, isDashedLine);
    }
    
    public LineEdgePainter(JPowerGraphColor blackAndWhite, JPowerGraphColor dragging, JPowerGraphColor normal, boolean isDashedLine){
        edgeColor1 = blackAndWhite;
        edgeColor2 = dragging;
        edgeColor3 = normal;
        dashedLine = isDashedLine;
    }
    
    public void paintEdge(JGraphPane graphPane, JPowerGraphGraphics g, T edge, SubGraphHighlighter theSubGraphHighlighter) {
        JPowerGraphPoint from = graphPane.getScreenPointForNode(edge.getFrom());
        JPowerGraphPoint to = graphPane.getScreenPointForNode(edge.getTo());
        JPowerGraphColor oldFGColor = g.getForeground();
        JPowerGraphColor oldBGColor = g.getBackground();
        
        g.setForeground(getEdgeColor(edge, graphPane, false, theSubGraphHighlighter));
        g.setBackground(getEdgeColor(edge, graphPane, false, theSubGraphHighlighter));
        paintArrow(g, from.x, from.y, to.x, to.y, dashedLine);
        g.setForeground(oldFGColor);
        g.setBackground(oldBGColor);
    }
    
    protected JPowerGraphColor getEdgeColor(Edge edge, JGraphPane graphPane, boolean isShowBlackAndWhite, SubGraphHighlighter theSubGraphHighlighter) {
        HighlightingManipulator highlightingManipulator = (HighlightingManipulator) graphPane.getManipulator(HighlightingManipulator.NAME);
        boolean isHighlighted = highlightingManipulator != null && highlightingManipulator.getHighlightedEdge() == edge;
        DraggingManipulator draggingManipulator = (DraggingManipulator) graphPane.getManipulator(DraggingManipulator.NAME);
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
    
    public static void paintArrow(JPowerGraphGraphics g,int x1,int y1,int x2,int y2, boolean isDashedLine) {
        double middleX=(x1+x2)/2;
        double middleY=(y1+y2)/2;
        double slope=Math.atan2(y2-y1,x2-x1);
        double pinnacleX=middleX+SQUARE_ROOT_OF_3_OVER_2*ARROW_BASE_LENGTH*Math.cos(slope);
        double pinnacleY=middleY+SQUARE_ROOT_OF_3_OVER_2*ARROW_BASE_LENGTH*Math.sin(slope);
        double backwardX=pinnacleX+ARROW_BASE_LENGTH*Math.cos(slope+Math.PI+Math.PI/6.0);
        double backwardY=pinnacleY+ARROW_BASE_LENGTH*Math.sin(slope+Math.PI+Math.PI/6.0);
        double forwardX=pinnacleX+ARROW_BASE_LENGTH*Math.cos(slope+Math.PI-Math.PI/6.0);
        double forwardY=pinnacleY+ARROW_BASE_LENGTH*Math.sin(slope+Math.PI-Math.PI/6.0);
        double baseX=(forwardX+backwardX)/2.0;
        double baseY=(forwardY+backwardY)/2.0;
        g.setLineDashed(isDashedLine);
        g.drawLine(x1,y1,(int)baseX,(int)baseY);
        g.drawLine((int)pinnacleX,(int)pinnacleY,x2,y2);
        g.setLineDashed(false);
        g.fillPolygon(new int[]{(int)backwardX, (int)backwardY, (int)pinnacleX, (int)pinnacleY, (int)forwardX, (int)forwardY});
    }
}
