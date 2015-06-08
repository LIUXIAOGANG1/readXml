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

import net.sourceforge.jpowergraph.lens.CursorLens;
import net.sourceforge.jpowergraph.manipulator.AbstractManipulator;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;
import net.sourceforge.jpowergraph.swtswinginteraction.listeners.JPowerGraphMouseEvent;


public class BackgroundDraggingManipulator extends AbstractManipulator {

    public static final String NAME = "BackgroundDraggingManipulator";

    private JPowerGraphPoint m_grabPosition;    
    private CursorLens cursorLens;
    private int modifier = JPowerGraphMouseEvent.NO_MODIFIER;
    
    public BackgroundDraggingManipulator(CursorLens theCursorLens) {
        this(theCursorLens, JPowerGraphMouseEvent.SHIFT_MODIFIER);
    }

    public BackgroundDraggingManipulator(CursorLens theCursorLens, int theModifier) {
        this.cursorLens = theCursorLens;
        this.modifier = theModifier;
    }
    
    public String getName() {
        return NAME;
    }
    
    public void mouseDown(JPowerGraphMouseEvent e) {
        if (getGraphPane().isEnabled() && e.getButton() == JPowerGraphMouseEvent.LEFT && isStartBackgroundDraggingEvent(e)) {
            if (getGraphPane().getNodeAtPoint(e.getPoint())==null && getGraphPane().getNearestEdge(e.getPoint())==null) {
                m_grabPosition = e.getPoint();
                cursorLens.setHand(true);
            }
        }
    }
    public void mouseUp(JPowerGraphMouseEvent e) {
        if (isDragging()) {
            performDrag(e.getPoint());
            cursorLens.setHand(false);
            m_grabPosition=null;
        }
    }
    
    public void mouseMove(JPowerGraphMouseEvent e) {
        if (isDragging()) {
            performDrag(e.getPoint());
        }
    }
    
    private boolean isDragging() {
        return m_grabPosition != null;
    }
    
    private void performDrag(JPowerGraphPoint position) {
        JPowerGraphRectangle rectangle = getGraphPane().getVisibleRectangle();
        rectangle.x += m_grabPosition.x-position.x;
        rectangle.y += m_grabPosition.y-position.y;
        getGraphPane().scrollRectToVisible(rectangle);
        m_grabPosition=position;
    }
    
    private boolean isStartBackgroundDraggingEvent(JPowerGraphMouseEvent e) {
        if (modifier == JPowerGraphMouseEvent.NO_MODIFIER){
            return true;
        }
        if (e.getModifierList().size() != 1){
            return false;
        }
        return e.getModifierList().get(0) == modifier;
    }
}
