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

package net.sourceforge.jpowergraph.swing.manipulator;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.manipulator.popup.ContextMenuListener;
import net.sourceforge.jpowergraph.manipulator.popup.ToolTipListener;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.pane.PopupDisplayer;
import net.sourceforge.jpowergraph.swing.SwingJGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.listeners.JPowerGraphMouseEvent;


public class SwingPopupDisplayer implements PopupDisplayer{

    private JPopupMenu rightClick;
    private JPopupMenu tooltip;
    private ToolTipListener <JComponent, Color> toolTipListener;
    private ContextMenuListener <JPopupMenu> contextMenuListener;
    private Color background;
    private Node lastNode = null;
    
    public SwingPopupDisplayer(ToolTipListener <JComponent, Color> theToolTipListener, ContextMenuListener <JPopupMenu> theContextMenuListener){
        this.background = new Color(255, 255, 204);
        this.toolTipListener = theToolTipListener;
        this.contextMenuListener = theContextMenuListener;
    }

    public void doToolTipPopup(JGraphPane theGraphPane, JPowerGraphMouseEvent e) {
        SwingJGraphPane graphPane = (SwingJGraphPane) theGraphPane;
        
        JPowerGraphPoint point = e.getPoint();
        Node node = graphPane.getNodeAtPoint(point);

        if (node != null && node != lastNode) {
            closeToolTipIfNeeded(graphPane);
            tooltip = new JPopupMenu();
            tooltip.setBackground(background);
            if (toolTipListener != null){
                boolean okay = toolTipListener.addNodeToolTipItems(node, tooltip, background);
                if (okay){
                    tooltip.show(graphPane, point.x, point.y + 25);
                }
            }
        }
        else if (node == null){
            closeToolTipIfNeeded(graphPane);
        }
        lastNode = node;
    }
    
    public void closeToolTipIfNeeded(JGraphPane theGraphPane) {
        if (isToolTipShowing()) {
            tooltip.setVisible(false);
            tooltip = null;
        }
    }
    
    public boolean isToolTipShowing(){
        return tooltip != null && tooltip.isVisible();
    }

    public void doRightClickPopup(JGraphPane theGraphPane, JPowerGraphMouseEvent e) {
        SwingJGraphPane graphPane = (SwingJGraphPane) theGraphPane;
       
        JPowerGraphPoint point = e.getPoint();
        Legend legend =  graphPane.getLegendAtPoint(point);
        Node node = graphPane.getNodeAtPoint(point);
        Edge edge = graphPane.getNearestEdge(point);
        
        closeRightClickIfNeeded(graphPane);
        rightClick = new JPopupMenu();
        if (legend != null){
            if (contextMenuListener != null){
                contextMenuListener.fillLegendContextMenu(legend, rightClick);
            }
        }
        else if (node != null){
            if (contextMenuListener != null){
                contextMenuListener.fillNodeContextMenu(node, rightClick);
            }
        }
        else if (edge != null){
            if (contextMenuListener != null){
                contextMenuListener.fillEdgeContextMenu(edge, rightClick);
            }
        }
        else{
            if (contextMenuListener != null){
                contextMenuListener.fillBackgroundContextMenu(rightClick);
            }
        }
        
        if (rightClick.getComponentCount() > 0){
            closeToolTipIfNeeded(graphPane);
            rightClick.show(graphPane, point.x, point.y);
        }
    }
    
    public void closeRightClickIfNeeded(JGraphPane theGraphPane){
        if (isRightClickShowing()) {
            rightClick.setVisible(false);
            rightClick = null;
        }
    }
    
    public boolean isRightClickShowing(){
        return rightClick != null && rightClick.isVisible();
    }
}
