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

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.SubGraphHighlighter;
import net.sourceforge.jpowergraph.defaults.TextEdge;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;


public class LineWithTextEdgePainter <T extends TextEdge> extends LineEdgePainter <T> {

    public LineWithTextEdgePainter(){
        super();
    }
    
    public LineWithTextEdgePainter(JPowerGraphColor dragging, JPowerGraphColor normal, boolean isDashedLine){
        super(dragging, normal, isDashedLine);
    }
    
    
    public LineWithTextEdgePainter(JPowerGraphColor blackAndWhite, JPowerGraphColor dragging, JPowerGraphColor normal, boolean isDashedLine){
        super(blackAndWhite, dragging, normal, isDashedLine);
    }
    
    public void paintEdge(JGraphPane graphPane, JPowerGraphGraphics g, T edge, SubGraphHighlighter theSubGraphHighlighter) {
        super.paintEdge(graphPane, g, edge, theSubGraphHighlighter);
        String text = edge.getText();
        
        JPowerGraphPoint from = graphPane.getScreenPointForNode(edge.getFrom());
        JPowerGraphPoint to = graphPane.getScreenPointForNode(edge.getTo());
        Point2D midpoint = new Point2D.Double((from.x + to.x)/2, ((from.y + to.y)/2));
        
        double slopeTop = from.y - to.y;
        double slopeBottom = from.x - to.x;
        double slope = Double.POSITIVE_INFINITY;
        if (slopeBottom != 0){
            slope = slopeTop / slopeBottom;
        }
        
        int stringWidth = g.getStringWidth(text);
        
        if (slope > 2.0){
            double xDiff = - (stringWidth + 10);
            double yDiff = -10;
            if (from.y < to.y){
                yDiff = -5;
            }
            midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
        }
        else if (slope < -2.0){
            double xDiff = 10;
            double yDiff = -10;
            if (from.y < to.y){
                yDiff = -5;
            }
            midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
        }
        else if (slope > 0.4 || slope < -0.4){
            double xDiff = 10;
            double yDiff = -10;
            if (from.x > to.x){
                xDiff = -(stringWidth + 10);
            }
            if (from.y < to.y){
                yDiff = -5;
            }
            midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
        }
        else{
            double xDiff = - stringWidth/2;
            double yDiff = -20;
            midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
        }
        
        JPowerGraphColor oldFGColor=g.getForeground();
        g.setForeground(getEdgeColor(edge, graphPane, false, theSubGraphHighlighter));
        g.drawString(text, (int) midpoint.getX(), (int) midpoint.getY(), 1);
        g.setForeground(oldFGColor);
    }
}
