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

package net.sourceforge.jpowergraph.manipulator.dragging;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.lens.CursorLens;
import net.sourceforge.jpowergraph.manipulator.AbstractManipulator;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.listeners.JPowerGraphMouseEvent;


public class DraggingManipulator extends AbstractManipulator {

    public static final String NAME = "DraggingManipulator";
    
    protected JPowerGraphPoint m_lastPosition;
    protected Node m_draggedNode;
    protected Edge m_draggedEdge;
    protected JPowerGraphPoint m_grabPoint1;
    protected JPowerGraphPoint m_grabPoint2;
    protected boolean m_oldFixedState1;
    protected boolean m_oldFixedState2;
    
    private CursorLens cursorLens;
    
    private int modifier = JPowerGraphMouseEvent.NO_MODIFIER;
    
    public DraggingManipulator(CursorLens theCursorLens) {
        this(theCursorLens, JPowerGraphMouseEvent.NO_MODIFIER);
    }

    public DraggingManipulator(CursorLens theCursorLens, int theMouseEventKeyMask) {
        cursorLens = theCursorLens;
        modifier = theMouseEventKeyMask;
    }
    
    public String getName() {
        return NAME;
    }
    
    public void mouseDown(JPowerGraphMouseEvent e) {
        if (getGraphPane().isEnabled() && cursorLens.isArrow() && e.getButton() == JPowerGraphMouseEvent.LEFT && isStartDraggingEvent(e)) {
            m_draggedNode = getGraphPane().getNodeAtPoint(e.getPoint());
            if (m_draggedNode != null) {
                m_lastPosition = e.getPoint();
                JPowerGraphPoint nodeScreenPoint = getGraphPane().getScreenPointForNode(m_draggedNode);
                m_grabPoint1 = new JPowerGraphPoint(e.getPoint().x - nodeScreenPoint.x, e.getPoint().y - nodeScreenPoint.y);
                m_oldFixedState1 = m_draggedNode.isFixed();
                m_draggedNode.setFixed(true);
                cursorLens.setHand(true);
            }
            else {
                m_draggedEdge = getGraphPane().getNearestEdge(e.getPoint());
                if (m_draggedEdge != null) {
                    m_lastPosition = e.getPoint();
                    JPowerGraphPoint fromPoint = getGraphPane().getScreenPointForNode(m_draggedEdge.getFrom());
                    m_grabPoint1 = new JPowerGraphPoint(e.getPoint().x - fromPoint.x, e.getPoint().y - fromPoint.y);
                    JPowerGraphPoint toPoint = getGraphPane().getScreenPointForNode(m_draggedEdge.getTo());
                    m_grabPoint2 = new JPowerGraphPoint(e.getPoint().x - toPoint.x, e.getPoint().y - toPoint.y);
                    m_oldFixedState1 = m_draggedEdge.getFrom().isFixed();
                    m_oldFixedState1 = m_draggedEdge.getTo().isFixed();
                    m_draggedEdge.getFrom().setFixed(true);
                    m_draggedEdge.getTo().setFixed(true);
                    cursorLens.setHand(true);
                }
            }
        }
    }
    
    public void mouseUp(JPowerGraphMouseEvent e) {
        if (m_draggedNode!=null) {
            moveDraggedNode(e.getPoint(), false);
            m_draggedNode.setFixed(m_oldFixedState1);
        }
        if (m_draggedEdge!=null) {
            moveDraggedEdge(e.getPoint(), false);
            m_draggedEdge.getFrom().setFixed(m_oldFixedState1);
            m_draggedEdge.getTo().setFixed(m_oldFixedState2);
        }
        finishDragging();
    }

    public void mouseMove(JPowerGraphMouseEvent e) {
        if (m_draggedNode!=null) {
            autoscroll(e);
            moveDraggedNode(e.getPoint(), true);
        }
        if (m_draggedEdge!=null) {
            autoscroll(e);
            moveDraggedEdge(e.getPoint(), true);
        }
    }
    
    public void finishDragging(){
        cursorLens.setHand(false);
        m_grabPoint1 = null;
        m_grabPoint2 = null;
        m_draggedNode = null;
        m_draggedEdge = null;
        m_lastPosition = null;
    }
    
    private boolean isStartDraggingEvent(JPowerGraphMouseEvent e) {
        if (modifier == JPowerGraphMouseEvent.NO_MODIFIER){
            return true;
        }
        if (e.getModifierList().size() != 1){
            return false;
        }
        return e.getModifierList().get(0) == modifier;
    }
    
    public Node getDraggedNode() {
        return m_draggedNode;
    }
    
    public Edge getDraggedEdge() {
        return m_draggedEdge;
    }
    
    protected void moveDraggedNode(JPowerGraphPoint point, boolean stillMoving) {
        if (!point.equals(m_lastPosition)) {
            JPowerGraphPoint tempLastPoint = new JPowerGraphPoint(point.x, point.y);
            point.x-=m_grabPoint1.x;
            point.y-=m_grabPoint1.y;
            Point2D graphPoint = new Point2D.Double();
            getGraphPane().screenToGraphPoint(point,graphPoint);
            
            double deltax = graphPoint.getX() - m_draggedNode.getX();
            if (deltax < 0){
                deltax *= -1;
            }
            double deltay = graphPoint.getY() - m_draggedNode.getY();
            if (deltay < 0){
                deltay *= -1;
            }
            if (deltax >= 15 || deltay >= 15 || !stillMoving){
                m_lastPosition.x = tempLastPoint.x;
                m_lastPosition.y = tempLastPoint.y;
                m_draggedNode.setLocation(graphPoint.getX(),graphPoint.getY());
                getGraphPane().getGraph().notifyLayoutUpdated();
            }
        }
    }
    
    protected void moveDraggedEdge(JPowerGraphPoint point, boolean stillMoving) {
        if (!point.equals(m_lastPosition)) {
            JPowerGraphPoint tempLastPoint = new JPowerGraphPoint(point.x, point.y);
            JPowerGraphPoint p1 = new JPowerGraphPoint(point.x-m_grabPoint1.x,point.y-m_grabPoint1.y);
            JPowerGraphPoint p2 = new JPowerGraphPoint(point.x-m_grabPoint2.x,point.y-m_grabPoint2.y);
            
            Point2D graphPoint1=new Point2D.Double();
            Point2D graphPoint2=new Point2D.Double();
            getGraphPane().screenToGraphPoint(p1, graphPoint1);
            getGraphPane().screenToGraphPoint(p2, graphPoint2);
            
            double deltax1 = graphPoint1.getX() - m_draggedEdge.getFrom().getX();
            if (deltax1 < 0){
                deltax1 *= -1;
            }
            double deltay1 = graphPoint1.getY() - m_draggedEdge.getFrom().getY();
            if (deltay1 < 0){
                deltay1 *= -1;
            }
            double deltax2 = graphPoint2.getX() - m_draggedEdge.getTo().getX();
            if (deltax2 < 0){
                deltax2 *= -1;
            }
            double deltay2 = graphPoint2.getY() - m_draggedEdge.getFrom().getY();
            if (deltay2 < 0){
                deltay2 *= -1;
            }
            
            if (deltax1 >= 15 || deltay1 >= 15 || deltax2 >= 15 || deltay2 >= 15 || !stillMoving){
                m_lastPosition.x = tempLastPoint.x;
                m_lastPosition.y = tempLastPoint.y;
                m_draggedEdge.getFrom().setLocation(graphPoint1.getX(),graphPoint1.getY());
                m_draggedEdge.getTo().setLocation(graphPoint2.getX(),graphPoint2.getY());
                getGraphPane().getGraph().notifyLayoutUpdated();
            }
        }
    }
}
