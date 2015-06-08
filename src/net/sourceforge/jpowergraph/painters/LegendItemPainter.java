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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sourceforge.jpowergraph.GroupLegendItem;
import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.LegendItem;
import net.sourceforge.jpowergraph.NodeLegendItem;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphDimension;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;


public class LegendItemPainter {

    private JPowerGraphColor enabledButtonColor;
    private JPowerGraphColor disabledButtonColor;
    
    private int buttonWidth = 10;
    
    private int toggleWidth = 18;
    private int toggleHeight = 8;
    
    private int widthPadding = 4;
    private int heightpadding = 1;
    
    private int indent = 18;

    public LegendItemPainter(){
        this(JPowerGraphColor.BLACK, JPowerGraphColor.LIGHT_GRAY);
    }
    
    public LegendItemPainter(JPowerGraphColor theEnabledColor, JPowerGraphColor theDisabledColor) {
        this.enabledButtonColor = theEnabledColor;
        this.disabledButtonColor = theDisabledColor;
    }

    public void paintLegendItem(final JGraphPane graphPane, JPowerGraphGraphics g, LegendItem legendItem, Legend theLegend, JPowerGraphPoint thePoint, JPowerGraphRectangle theLegendRectangle) {
        if (legendItem instanceof NodeLegendItem){
            final NodeLegendItem nodeLegendItem = (NodeLegendItem) legendItem;

            JPowerGraphDimension d = nodeLegendItem.getNodePainter().getLegendItemSize(graphPane, nodeLegendItem.getDescription());
            nodeLegendItem.getNodePainter().paintLegendItem(g, thePoint, nodeLegendItem.getDescription());
            
            if (graphPane.getGraph().getNodeFilter() != null && theLegend.isInteractive()){
                int buttonX = theLegendRectangle.x + (theLegendRectangle.width - (buttonWidth + widthPadding));
                int buttonY = thePoint.y - heightpadding;
                int buttonHeight = d.getHeight() - 5;
                
                boolean canFilterNode = graphPane.getGraph().getNodeFilter().canChangeFilterState(nodeLegendItem.getNodeClass());
                boolean isChecked = graphPane.getGraph().getNodeFilter().getFilterState(nodeLegendItem.getNodeClass());
                JPowerGraphRectangle r = new JPowerGraphRectangle(buttonX, buttonY, buttonWidth, buttonHeight);
                if (canFilterNode){
                    g.setForeground(enabledButtonColor);
                    theLegend.addActionRectangle(r, new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            graphPane.getGraph().getNodeFilter().setFilterState(nodeLegendItem.getNodeClass(), !graphPane.getGraph().getNodeFilter().getFilterState(nodeLegendItem.getNodeClass()));
                        }
                    });
                }
                else{
                    g.setForeground(disabledButtonColor);
                }
                
                g.drawRectangle(r.x, r.y, r.width, r.height);
                if (isChecked){
                    g.drawLine(r.x + 3, r.y + 3, r.x + r.width - 3, r.y + r.height - 3);
                    g.drawLine(r.x + 3, r.y + r.height - 3, r.x + r.width - 3, r.y + 3);
                }
            }
        }
        else if (legendItem instanceof GroupLegendItem){
            final GroupLegendItem groupLegendItem = (GroupLegendItem) legendItem;
            if (groupLegendItem.getLegendItems().size() > 0){
                JPowerGraphRectangle r = new JPowerGraphRectangle(thePoint.x + 5, thePoint.y, toggleWidth - 10, toggleHeight);
                
                theLegend.addActionRectangle(r, new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        groupLegendItem.setExpanded(!groupLegendItem.isExpanded());
                        graphPane.redraw();
                    }
                });
                
                g.setForeground(enabledButtonColor);
                g.drawLine(r.x, r.y + r.height/2, r.x + r.width, r.y + r.height/2);
                g.drawString(groupLegendItem.getDescription(), r.x + r.width + 5, r.y - 2, 1);
                if (groupLegendItem.isExpanded()){
                    JPowerGraphPoint subPoint = new JPowerGraphPoint(thePoint.x + indent, thePoint.y + Math.max(toggleHeight, g.getAscent() + g.getDescent() + 4));
                    for (LegendItem subLegendItem : groupLegendItem.getLegendItems()) {
                        JPowerGraphDimension subDimension = new JPowerGraphDimension(0, 0);
                        getLegendItemSize(graphPane, subLegendItem, theLegend, subDimension);
                        paintLegendItem(graphPane, g, subLegendItem, theLegend, subPoint, theLegendRectangle);
                        subPoint.y += subDimension.height;
                    }
                }
                else{
                    g.drawLine(r.x + r.width/2, r.y, r.x + r.width/2, r.y + r.height);
                }
            }
        }
    }
    
    public void getLegendItemSize(JGraphPane graphPane, LegendItem legendItem, Legend theLegend, JPowerGraphDimension theDimension) {
        if (legendItem instanceof NodeLegendItem){
            JPowerGraphDimension dimension = ((NodeLegendItem) legendItem).getNodePainter().getLegendItemSize(graphPane, legendItem.getDescription());
            theDimension.width = Math.max(theDimension.width, dimension.width);
            theDimension.height += dimension.height; 
            
            if (graphPane.getGraph().getNodeFilter() != null  && theLegend.isInteractive()){
                theDimension.width += buttonWidth + widthPadding;
            }
        }
        else if (legendItem instanceof GroupLegendItem){
            if (((GroupLegendItem) legendItem).getLegendItems().size() > 0){
                JPowerGraphGraphics g = graphPane.getJPowerGraphGraphics();
                theDimension.width = Math.max(theDimension.width, g.getStringWidth(((GroupLegendItem) legendItem).getDescription()) + toggleWidth + 5);
                theDimension.height += Math.max(toggleHeight, g.getAscent() + g.getDescent() + 4);
                
                if (((GroupLegendItem) legendItem).isExpanded()){
                    for (LegendItem subLegendItem : ((GroupLegendItem) legendItem).getLegendItems()) {
                        JPowerGraphDimension subDimension = new JPowerGraphDimension (0, 0);
                        this.getLegendItemSize(graphPane, subLegendItem, theLegend, subDimension);
                        theDimension.width = Math.max(theDimension.width, subDimension.width + indent);
                        theDimension.height += subDimension.height; 
                    }
                }
            }
        }
    }
}
