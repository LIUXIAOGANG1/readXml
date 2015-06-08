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

package net.sourceforge.jpowergraph.manipulator.edge;

import java.awt.geom.Line2D;

import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.lens.CursorLens;
import net.sourceforge.jpowergraph.manipulator.AbstractManipulator;
import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.listeners.JPowerGraphMouseEvent;


public class EdgeCreatorManipulator extends AbstractManipulator {

    public static final String NAME = "EdgeCreatorManipulator";

    protected Node m_draggedNode;
    private Line2D draggingLine;
    private CursorLens cursorLens;
    private EdgeCreatorListener edgeCreatorListener;
    
    public EdgeCreatorManipulator(CursorLens theCursorLens, EdgeCreatorListener theEdgeCreatorListener) {
        this.cursorLens = theCursorLens;
        this.edgeCreatorListener = theEdgeCreatorListener;
    }
    
    public String getName() {
        return NAME;
    }
    
    public void mouseDown(JPowerGraphMouseEvent e) {
        if (getGraphPane().isEnabled() && cursorLens.isCross() && e.getButton() == JPowerGraphMouseEvent.LEFT) {
            m_draggedNode = getGraphPane().getNodeAtPoint(e.getPoint());
            if (m_draggedNode == null || !edgeCreatorListener.canCreateEdgeFrom(m_draggedNode)) {
                m_draggedNode = null;
                cursorLens.setStop(true);
            }
            else{
                cursorLens.setStop(false);
            }
        }
    }
    
    public void mouseUp(JPowerGraphMouseEvent e) {
        if (m_draggedNode!=null) {
            Node overChild = getGraphPane().getNodeAtPoint(e.getPoint());
            boolean canLinkHere = edgeCreatorListener.canCreateEdge(m_draggedNode, overChild);
            if (canLinkHere){
                edgeCreatorListener.createEdge(m_draggedNode, overChild);
            }
        }
        m_draggedNode = null;
        draggingLine = null;
        cursorLens.setStop(false);
        synchronized (getGraphPane()){
            getGraphPane().redraw();
        }
    }
    
    public void mouseMove(JPowerGraphMouseEvent e) {
        if (m_draggedNode!=null) {
            autoscroll(e);
            Node overChild = getGraphPane().getNodeAtPoint(e.getPoint());
            boolean canLinkHere = edgeCreatorListener.canCreateEdge(m_draggedNode, overChild);
            if (!canLinkHere && overChild != null){
                cursorLens.setStop(true);
            }
            else{
                cursorLens.setStop(false);
            }
            
            JPowerGraphPoint nodeScreenPoint = getGraphPane().getScreenPointForNode(m_draggedNode);
            draggingLine = new Line2D.Double(nodeScreenPoint.x, nodeScreenPoint.y, e.getPoint().x, e.getPoint().y);
            synchronized (getGraphPane()){
                getGraphPane().redraw();
            }
        }
        else{
            draggingLine = null;
        }
    }
    
    public boolean isDragging() {
        return m_draggedNode!=null;
    }
    
    public Node getDraggedNode() {
        return m_draggedNode;
    }
    
    public void paint(JPowerGraphGraphics g) {
        if (draggingLine != null){
            g.setForeground(((ShapeNodePainter) getGraphPane().getPainterForNode(m_draggedNode)).getBorderColor());
            g.drawLine((int) draggingLine.getX1(), (int) draggingLine.getY1(), (int) draggingLine.getX2(), (int) draggingLine.getY2());
        }
    }
}
