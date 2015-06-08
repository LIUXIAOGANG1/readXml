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

package net.sourceforge.jpowergraph.painters;

import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.LegendItem;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphDimension;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;

public class LegendPainter {

    private JPowerGraphColor white;
    private JPowerGraphColor black;
    
    public LegendPainter(){
        this.white = JPowerGraphColor.WHITE;
        this.black = JPowerGraphColor.BLACK;
    }
    
    public JPowerGraphRectangle paintLegend(JGraphPane graphPane, JPowerGraphGraphics g, final Legend theLegend){
        int width = 0;
        int height = 0;
        int widthPad = 10;
        int heightPad = 8;
        LegendItemPainter painter = new LegendItemPainter();
        
        height = heightPad;
        for (LegendItem legendItem : theLegend.getRoot().getLegendItems()) {
            JPowerGraphDimension dimension = new JPowerGraphDimension(0, 0);
            painter.getLegendItemSize(graphPane, legendItem, theLegend, dimension);
            if (dimension.width != widthPad && dimension.height != heightPad){
                width = Math.max(width, dimension.width + widthPad);
                height += dimension.height;
            }
        }
        JPowerGraphRectangle legendRectangle = new JPowerGraphRectangle(10, graphPane.getHeight() - (height + 10), width, height);
        
        JPowerGraphColor oldColor = g.getForeground();
        g.setForeground(white);
        g.fillRoundRectangle(legendRectangle.x, legendRectangle.y, legendRectangle.width, legendRectangle.height, 10, 10);
        g.setForeground(black);
        g.drawRoundRectangle(legendRectangle.x, legendRectangle.y, legendRectangle.width, legendRectangle.height, 10, 10);
        g.setForeground(oldColor);
        
        JPowerGraphPoint point = new JPowerGraphPoint(legendRectangle.x + widthPad/2, legendRectangle.y + heightPad);
        for (LegendItem legendItem : theLegend.getRoot().getLegendItems()) {
            JPowerGraphDimension dimension = new JPowerGraphDimension(0, 0);
            painter.getLegendItemSize(graphPane, legendItem, theLegend, dimension);
            painter.paintLegendItem(graphPane, g, legendItem, theLegend, point, legendRectangle);
            point.y += dimension.height;
        }
        
        return legendRectangle;
    }
}
