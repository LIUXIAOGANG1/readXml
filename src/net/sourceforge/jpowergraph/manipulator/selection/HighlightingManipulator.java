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

import java.beans.PropertyChangeSupport;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.manipulator.AbstractManipulator;
import net.sourceforge.jpowergraph.swtswinginteraction.listeners.JPowerGraphMouseEvent;


public class HighlightingManipulator extends AbstractManipulator{
    public static final String NAME="HighlightingManipulator";
    
    protected PropertyChangeSupport m_propertyChangeSupport;
    protected Node m_highlightedNode;
    protected Edge m_highlightedEdge;
    
    public HighlightingManipulator() {
        this.m_propertyChangeSupport = new PropertyChangeSupport(this);
    }
    public String getName() {
        return NAME;
    }
    
    public void mouseEnter(JPowerGraphMouseEvent e) {
        updateHighlight(e);
    }
    
    public void mouseExit(JPowerGraphMouseEvent e) {
        setHighlightedNode(null);
        setHighlightedEdge(null);
    }
    
    public void mouseMove(JPowerGraphMouseEvent e) {
        updateHighlight(e);
    }
    
    protected void updateHighlight(JPowerGraphMouseEvent e) {
        if (getGraphPane().isEnabled()) {
            Node nodeAtPoint = getGraphPane().getNodeAtPoint(e.getPoint());
            if (nodeAtPoint != null) {
                setHighlightedNode(nodeAtPoint);
                setHighlightedEdge(null);
            }
            else {
                setHighlightedNode(null);
                Edge edgeAtPoint=getGraphPane().getNearestEdge(e.getPoint());
                setHighlightedEdge(edgeAtPoint);
            }
        }
        else {
            setHighlightedNode(null);
            setHighlightedEdge(null);
        }
    }
    
    public Node getHighlightedNode() {
        return m_highlightedNode;
    }
    
    public void setHighlightedNode(Node highlightedNode) {
        if (highlightedNode != m_highlightedNode) {
            Node oldHighlightedNode = m_highlightedNode;
            if (oldHighlightedNode != null){
                getGraphPane().repaintNode(oldHighlightedNode);
            }
            m_highlightedNode = highlightedNode;
            if (m_highlightedNode != null){
                getGraphPane().repaintNode(m_highlightedNode);
            }
            m_propertyChangeSupport.firePropertyChange("highlightedNode", oldHighlightedNode, m_highlightedNode);
        }
    }

    public Edge getHighlightedEdge() {
        return m_highlightedEdge;
    }
    
    public void setHighlightedEdge(Edge highlightedEdge) {
        if (highlightedEdge != m_highlightedEdge) {
            Edge oldHighlightedEdge = m_highlightedEdge;
            if (oldHighlightedEdge != null){
                getGraphPane().repaintEdge(oldHighlightedEdge);
            }
            m_highlightedEdge = highlightedEdge;
            if (m_highlightedEdge != null){
                getGraphPane().repaintEdge(m_highlightedEdge);
            }
            m_propertyChangeSupport.firePropertyChange("highlightedEdge", oldHighlightedEdge, m_highlightedEdge);
        }
    }
}
