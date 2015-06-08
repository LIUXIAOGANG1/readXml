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

package net.sourceforge.jpowergraph.pane;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.SubGraphHighlighter;
import net.sourceforge.jpowergraph.lens.Lens;
import net.sourceforge.jpowergraph.manipulator.Manipulator;
import net.sourceforge.jpowergraph.painters.EdgePainter;
import net.sourceforge.jpowergraph.painters.NodePainter;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;


public interface JGraphPane {

    public JPowerGraphPoint getScreenPointForNode(Node from);
    public void getNodeScreenBounds(Node from, JPowerGraphRectangle rectangle);
    public Graph getGraph();
    public int getWidth();
    public int getHeight();
    public JPowerGraphGraphics getJPowerGraphGraphics();
    
    public JPowerGraphPoint getScreenLocation();
    
    public JPowerGraphRectangle getVisibleRectangle();
    public void scrollRectToVisible(JPowerGraphRectangle theRectangle);
    public void scrollRectToVisible(JPowerGraphPoint thePoint);
    
    void setPopupDisplayer(PopupDisplayer popupDisplayer);
    public PopupDisplayer getPopupDisplayer();
    public boolean isEnabled();
    
    public Legend getLegendAtPoint(JPowerGraphPoint point);
    public Node getNodeAtPoint(JPowerGraphPoint point);    
    public Edge getNearestEdge(JPowerGraphPoint point);
    public void redraw();
    public void setNodeSize(int nodeSize);
    public CursorChanger getCursorChanger();
    public void screenToGraphPoint(JPowerGraphPoint point, Point2D graphPoint);
    public Manipulator getManipulator(String name);
    public void repaintNode(Node theNode);
    public void repaintEdge(Edge theEdge);
    public NodePainter getPainterForNode(Node node);
    public void setLens(Lens lens);
    public SubGraphHighlighter getSubGraphHighlighter();
    public void addManipulator(Manipulator manipulator);
    public void setNodePainter(Class c, NodePainter painter);
    public void setEdgePainter(Class c, EdgePainter painter);
    public void setDefaultEdgePainter(EdgePainter painter);
    public void setAntialias(boolean b);
    Legend getLegend();
}
