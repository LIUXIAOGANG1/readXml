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

package net.sourceforge.jpowergraph.manipulator;

import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.listeners.JPowerGraphFocusEvent;
import net.sourceforge.jpowergraph.swtswinginteraction.listeners.JPowerGraphKeyEvent;
import net.sourceforge.jpowergraph.swtswinginteraction.listeners.JPowerGraphMouseEvent;


public abstract class AbstractManipulator implements Manipulator{

    private JGraphPane graphPane;
    private JPowerGraphPoint lastMouseEventScreenPoint;
    
    public void setGraphPane(JGraphPane theGraphPane) {
        this.graphPane = theGraphPane;
    }
    
    public JGraphPane getGraphPane() {
        return graphPane;
    }
    
    public JPowerGraphPoint getLastMouseEventScreenPoint() {
        return lastMouseEventScreenPoint;
    }
    
    public String getName() {
        return null;
    }
    
    public void paint(JPowerGraphGraphics g) {
    }
    
    public void notifyGraphPaneScrolled() {
    }
    
    protected void updateLastMouseEventScreenPoint(JPowerGraphMouseEvent e) {
        lastMouseEventScreenPoint=graphPane.getScreenLocation();
        lastMouseEventScreenPoint.x += e.getPoint().getX();
        lastMouseEventScreenPoint.y += e.getPoint().getY();
    }
    protected JPowerGraphPoint getLastMouseEventPoint() {
        JPowerGraphPoint point = graphPane.getScreenLocation();
        point.x += lastMouseEventScreenPoint.x - point.x;
        point.y += lastMouseEventScreenPoint.y - point.y;
        return point;
    }
    protected void autoscroll(JPowerGraphMouseEvent e) {
        if (!graphPane.getVisibleRectangle().contains(e.getPoint())){
            graphPane.scrollRectToVisible(e.getPoint());
        }
    }
    
    //TODO SWING  MAP
    // public void mouseClicked(MouseEvent e) {}
//    public void keyTyped(KeyEvent e) {}
    
    public void mouseDoubleClick(JPowerGraphMouseEvent arg0) {}
    public void mouseDown(JPowerGraphMouseEvent arg0) {} //mousePressed
    public void mouseUp(JPowerGraphMouseEvent arg0) {} // mouseReleased
    public void mouseEnter(JPowerGraphMouseEvent arg0) {}
    public void mouseExit(JPowerGraphMouseEvent arg0) {}
    public void mouseHover(JPowerGraphMouseEvent arg0) {}
    public void mouseMove(JPowerGraphMouseEvent arg0) {} //mouseDragged (+mouseDown)
    
    public void keyPressed(JPowerGraphKeyEvent theJPowerGraphKeyEvent){}
    public void keyReleased(JPowerGraphKeyEvent theJPowerGraphKeyEvent){}
    
    public void focusGained(JPowerGraphFocusEvent theJPowerGraphFocusEvent) {}
    public void focusLost(JPowerGraphFocusEvent theJPowerGraphFocusEvent) {}
}