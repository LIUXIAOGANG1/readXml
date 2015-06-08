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

package net.sourceforge.jpowergraph.manipulator.selection;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Collection;

import javax.swing.Action;

import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.manipulator.AbstractManipulator;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.listeners.JPowerGraphMouseEvent;


public class SelectionManipulator extends AbstractManipulator {

    public static final String NAME="SelectionManipulator";
    
    protected NodeSelectionModel m_nodeSelectionModel;
    protected Point2D m_selectionRectangleFixedGraph;
    protected JPowerGraphPoint m_selectionRectangleFixed;
    protected JPowerGraphPoint m_selectionRectangleMoving;
    
    private int selectionModifier;
    private int toggleModifier;

    public SelectionManipulator(NodeSelectionModel nodeSelectionModel) {
        this(nodeSelectionModel, JPowerGraphMouseEvent.SHIFT_MODIFIER, JPowerGraphMouseEvent.CTRL_MODIFIER);
    }

    public SelectionManipulator(NodeSelectionModel nodeSelectionModel, int theSelectionModifier, int theToggleModifier) {
        this.m_nodeSelectionModel=nodeSelectionModel;
        this.selectionModifier = theSelectionModifier;
        this.toggleModifier = theToggleModifier;
        this.m_nodeSelectionModel.addNodeSelectionListener(new NodeSelectionHandler());
        this.m_selectionRectangleFixedGraph=new Point2D.Double();
    }

    public String getName() {
        return NAME;
    }
    
    public void mouseDown(JPowerGraphMouseEvent e) {
        if (getGraphPane().isEnabled() && e.getButton() == JPowerGraphMouseEvent.LEFT) {
            Legend legend = getGraphPane().getLegendAtPoint(e.getPoint());
            Node node=getGraphPane().getNodeAtPoint(e.getPoint());
            if (legend != null){
                Action a = legend.getActionAtPoint(e.getPoint());
                if (a != null){
                    a.actionPerformed(new ActionEvent("Selection Source", 0, "do Action")); // null was e.getSource()
                }
            }
            else if (node != null) {
                if (isSelectEvent(e) && !m_nodeSelectionModel.isNodeSelected(node) && shouldSelectNode(node)){
                    m_nodeSelectionModel.addNode(node);
                }
                else if (isToggleEvent(e)){
                    if (m_nodeSelectionModel.isNodeSelected(node)){
                        m_nodeSelectionModel.removeNode(node);
                    }
                    else if (shouldSelectNode(node)){
                        m_nodeSelectionModel.addNode(node);
                    }
                }
                else if (!m_nodeSelectionModel.isNodeSelected(node) && shouldSelectNode(node)) {
                    m_nodeSelectionModel.clear();
                    m_nodeSelectionModel.addNode(node);
                }
            }
        }
    }
    
    public NodeSelectionModel getNodeSelectionModel() {
        return m_nodeSelectionModel;
    }
    
    private boolean shouldSelectNode(Node node) {
        return node != null;
    }
    
    private boolean isSelectEvent(JPowerGraphMouseEvent e) {
        if (selectionModifier == JPowerGraphMouseEvent.NO_MODIFIER){
            return true;
        }
        if (e.getModifierList().size() != 1){
            return false;
        }
        return e.getModifierList().get(0) == selectionModifier;
    }
    
    private boolean isToggleEvent(JPowerGraphMouseEvent e) {
        if (toggleModifier == JPowerGraphMouseEvent.NO_MODIFIER){
            return true;
        }
        if (e.getModifierList().size() != 1){
            return false;
        }
        return e.getModifierList().get(0) == toggleModifier;
    }
    
    protected class NodeSelectionHandler implements NodeSelectionListener {
        public void nodesAddedToSelection(NodeSelectionModel nodeSelectionModel,Collection nodes) {
            synchronized (SelectionManipulator.this.getGraphPane()){
                SelectionManipulator.this.getGraphPane().redraw();
            }
        }
        public void nodesRemovedFromSelection(NodeSelectionModel nodeSelectionModel,Collection nodes) {
            synchronized (SelectionManipulator.this.getGraphPane()){
                SelectionManipulator.this.getGraphPane().redraw();
            }
        }
        public void selectionCleared(NodeSelectionModel nodeSelectionModel) {
            synchronized (SelectionManipulator.this.getGraphPane()){
                SelectionManipulator.this.getGraphPane().redraw();
            }
        }
    }
}
