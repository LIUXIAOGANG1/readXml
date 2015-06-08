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

package net.sourceforge.jpowergraph.painters.node;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.SubGraphHighlighter;
import net.sourceforge.jpowergraph.defaults.DefaultNode;
import net.sourceforge.jpowergraph.defaults.DefaultSubGraphHighlighter;
import net.sourceforge.jpowergraph.manipulator.dragging.DraggingManipulator;
import net.sourceforge.jpowergraph.manipulator.selection.HighlightingManipulator;
import net.sourceforge.jpowergraph.manipulator.selection.SelectionManipulator;
import net.sourceforge.jpowergraph.painters.NodePainter;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphDimension;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;

public class ShapeNodePainter implements NodePainter <Node>{

    public static final int RECTANGLE = 0;
    public static final int ELLIPSE = 1;
    public static final int TRIANGLE = 2;

    private JPowerGraphColor notHighlightedBackgroundColor;
    private JPowerGraphColor notHighlightedBorderColor;
    private JPowerGraphColor notHighlightedTextColor;
    private JPowerGraphColor backgroundColor;
    private JPowerGraphColor borderColor;
    private JPowerGraphColor textColor;
    private int shape;

    public ShapeNodePainter(int theShape) {
        this(theShape, JPowerGraphColor.LIGHT_GRAY, JPowerGraphColor.DARK_GRAY, JPowerGraphColor.BLACK);
    }

    public ShapeNodePainter(int theShape, JPowerGraphColor theBackgroundColor, JPowerGraphColor theBorderColor, JPowerGraphColor theTextColor) {
        this.shape = theShape;
        this.backgroundColor = theBackgroundColor;
        this.borderColor = theBorderColor;
        this.textColor = theTextColor;

        this.notHighlightedBackgroundColor = new JPowerGraphColor(246, 246, 246);
        this.notHighlightedBorderColor = new JPowerGraphColor(197, 197, 197);
        this.notHighlightedTextColor = new JPowerGraphColor(197, 197, 197);
    }

    public void paintNode(JGraphPane graphPane, JPowerGraphGraphics g, Node node, int size, SubGraphHighlighter theSubGraphHighlighter) {
        JPowerGraphPoint nodePoint = graphPane.getScreenPointForNode(node);
        paintNode(graphPane, g, node, size, theSubGraphHighlighter, nodePoint);
    }
    
    public void paintNode(JGraphPane graphPane, JPowerGraphGraphics g, Node node, int size, SubGraphHighlighter theSubGraphHighlighter, JPowerGraphPoint thePoint) {
        paintNode(graphPane, g, node, size, theSubGraphHighlighter, thePoint, 1);
    }
    
    public void paintNode(JGraphPane graphPane, JPowerGraphGraphics g, Node node, int size, SubGraphHighlighter theSubGraphHighlighter, JPowerGraphPoint thePoint, double theScale) {
        if (size == NodePainter.LARGE){ 
            int width=20;
            int height=5;
            int textX=0;
            int textY=0;
            String label=node.getLabel();
            if (label!=null) {
                int stringWidth = stringWidth(g, label);
                int numlines = countLines(label);
                width += (stringWidth + (stringWidth/4));
                height += ((g.getAscent() + g.getDescent() + 10) * numlines);
                width = (int) Math.ceil(width * theScale);
                height = (int) Math.ceil(height * theScale);
                
                textX = (thePoint.x-stringWidth/2) + 1;
                textY = thePoint.y - (7 * numlines);
                if (shape == TRIANGLE){
                    textY += 7;
                }
            }
            else {
                width+=40;
                height+=20;
                width = (int) Math.ceil(width * theScale);
                height = (int) Math.ceil(height * theScale);
            }
            
            JPowerGraphColor oldBGColor = g.getBackground();
            JPowerGraphColor oldFGColor = g.getForeground();
            g.setBackground(getBackgroundColor(node, graphPane, theSubGraphHighlighter));
            if (shape == RECTANGLE){
                g.fillRectangle(thePoint.x-width/2,thePoint.y-height/2,width,height);
            }
            else if (shape == ELLIPSE){
                g.fillOval(thePoint.x-width/2,thePoint.y-height/2,width,height);
            }
            else if (shape == TRIANGLE){
                int x1 = thePoint.x - width/2;
                int y1 = thePoint.y + height/2;
                int x2 = x1 + width/2;
                int y2 = thePoint.y - height/2 - width/10;
                int x3 = x1 + width;
                int y3 = y1;
                
                g.fillPolygon(new int[]{x1, y1, x2, y2, x3, y3});
            }
            
            if (label!=null) {
                g.storeFont();
                g.setFontFromJGraphPane(graphPane);
                g.setForeground(getTextColor(node, graphPane, theSubGraphHighlighter));
                ArrayList <String> lines = getLines(label);
                for (int i = 0; i < lines.size(); i++){
                    int offset = (g.getAscent() + g.getDescent() + 2) * i;
                    g.drawString(lines.get(i), textX, textY + offset, lines.size());
                }
                g.restoreFont();
            }
            
            g.setForeground(getBorderColor(node, graphPane, theSubGraphHighlighter));
            if (shape == RECTANGLE){
                g.drawRectangle(thePoint.x-width/2,thePoint.y-height/2,width,height);
            }
            else if (shape == ELLIPSE){
                g.drawOval(thePoint.x-width/2,thePoint.y-height/2,width,height);
            }
            else if (shape == TRIANGLE){
                int x1 = thePoint.x - width/2;
                int y1 = thePoint.y + height/2;
                int x2 = x1 + width/2;
                int y2 = thePoint.y - height/2 - width/10;
                int x3 = x1 + width;
                int y3 = y1;
                
                g.drawPolygon(new int[]{x1, y1, x2, y2, x3, y3});
            }
            g.setBackground(oldBGColor);
            g.setForeground(oldFGColor);
        }
        else if (size == NodePainter.SMALL){
            int width=18;
            int height=8;
            width = (int) Math.ceil(width * theScale);
            height = (int) Math.ceil(height * theScale);
            int textX = thePoint.x + width;
            int textY = thePoint.y - 7;
            String label=node.getLabel();
            
            JPowerGraphColor oldBGColor = g.getBackground();
            JPowerGraphColor oldFGColor = g.getForeground();
            g.setBackground(getBackgroundColor(node, graphPane, theSubGraphHighlighter));
            if (shape == RECTANGLE){
                g.fillRectangle(thePoint.x-width/2,thePoint.y-height/2,width,height);
            }
            else if (shape == ELLIPSE){
                g.fillOval(thePoint.x-width/2,thePoint.y-height/2,width,height);
            }
            else if (shape == TRIANGLE){
                int x1 = thePoint.x - width/2;
                int y1 = thePoint.y + height/2;
                int x2 = x1 + width/2;
                int y2 = thePoint.y - height/2;
                int x3 = x1 + width;
                int y3 = y1;
                
                g.fillPolygon(new int[]{x1, y1, x2, y2, x3, y3});
            }

            if (label!=null) {
                g.storeFont();
                g.setFontFromJGraphPane(graphPane);
                g.setBackground(JPowerGraphColor.WHITE);
                g.setForeground(getTextColor(node, graphPane, theSubGraphHighlighter));
                ArrayList <String> lines = getLines(label);
                for (int i = 0; i < lines.size(); i++){
                    int offset = (g.getAscent() + g.getDescent() + 2) * i;
                    g.drawString(lines.get(i), textX, textY + offset, lines.size());
                }
                g.restoreFont();
            }
            
            g.setBackground(oldBGColor);
            g.setForeground(getBorderColor(node, graphPane, theSubGraphHighlighter));
            if (shape == RECTANGLE){
                g.drawRectangle(thePoint.x-width/2,thePoint.y-height/2,width,height);
            }
            else if (shape == ELLIPSE){
                g.drawOval(thePoint.x-width/2,thePoint.y-height/2,width,height);
            }
            else if (shape == TRIANGLE){
                int x1 = thePoint.x - width/2;
                int y1 = thePoint.y + height/2;
                int x2 = x1 + width/2;
                int y2 = thePoint.y - height/2;
                int x3 = x1 + width;
                int y3 = y1;
                
                g.drawPolygon(new int[]{x1, y1, x2, y2, x3, y3});
            }
            g.setBackground(oldBGColor);
            g.setForeground(oldFGColor);
        }
    }
    
    
    public JPowerGraphColor getBorderColor(Node theNode, JGraphPane theGraphPane, SubGraphHighlighter theSubGraphHighlighter) {
        HighlightingManipulator highlightingManipulator = null; 
        SelectionManipulator selectionManipulator = null;
        DraggingManipulator draggingManipulator = null;
        if (theGraphPane != null){
            highlightingManipulator = (HighlightingManipulator) theGraphPane.getManipulator(HighlightingManipulator.NAME);
            selectionManipulator = (SelectionManipulator) theGraphPane.getManipulator(SelectionManipulator.NAME);
            draggingManipulator = (DraggingManipulator) theGraphPane.getManipulator(DraggingManipulator.NAME);
        }
        
        boolean isHighlighted = highlightingManipulator != null && highlightingManipulator.getHighlightedNode() == theNode;
        boolean isSelected = selectionManipulator != null && selectionManipulator.getNodeSelectionModel().isNodeSelected(theNode);
        boolean isDragging = draggingManipulator != null && draggingManipulator.getDraggedNode() == theNode;
        boolean notHighlightedBecauseOfSubGraph = theSubGraphHighlighter.isHighlightSubGraphs() && !theSubGraphHighlighter.doesSubGraphContain(theNode);
        
        if (notHighlightedBecauseOfSubGraph) {
            return notHighlightedBorderColor;
        }
        else if (isHighlighted || isDragging || isSelected) {
            return backgroundColor;
        }
        return borderColor;
    }

    public JPowerGraphColor getBackgroundColor(Node theNode, JGraphPane theGraphPane, SubGraphHighlighter theSubGraphHighlighter) {
        HighlightingManipulator highlightingManipulator = null; 
        SelectionManipulator selectionManipulator = null;
        DraggingManipulator draggingManipulator = null;
        if (theGraphPane != null){
            highlightingManipulator = (HighlightingManipulator) theGraphPane.getManipulator(HighlightingManipulator.NAME);
            selectionManipulator = (SelectionManipulator) theGraphPane.getManipulator(SelectionManipulator.NAME);
            draggingManipulator = (DraggingManipulator) theGraphPane.getManipulator(DraggingManipulator.NAME);
        }
        
        boolean isHighlighted = highlightingManipulator != null && highlightingManipulator.getHighlightedNode() == theNode;
        boolean isSelected = selectionManipulator != null && selectionManipulator.getNodeSelectionModel().isNodeSelected(theNode);
        boolean isDragging = draggingManipulator != null && draggingManipulator.getDraggedNode() == theNode;
        boolean notHighlightedBecauseOfSubGraph = theSubGraphHighlighter.isHighlightSubGraphs() && !theSubGraphHighlighter.doesSubGraphContain(theNode);
        
        if (notHighlightedBecauseOfSubGraph) {
            return notHighlightedBackgroundColor;
        }
        else if (isHighlighted || isDragging || isSelected) {
            return borderColor;
        }
        return backgroundColor;
    }

    public JPowerGraphColor getTextColor(Node theNode, JGraphPane theGraphPane, SubGraphHighlighter theSubGraphHighlighter) {
        HighlightingManipulator highlightingManipulator = null; 
        SelectionManipulator selectionManipulator = null;
        DraggingManipulator draggingManipulator = null;
        if (theGraphPane != null){
            highlightingManipulator = (HighlightingManipulator) theGraphPane.getManipulator(HighlightingManipulator.NAME);
            selectionManipulator = (SelectionManipulator) theGraphPane.getManipulator(SelectionManipulator.NAME);
            draggingManipulator = (DraggingManipulator) theGraphPane.getManipulator(DraggingManipulator.NAME);
        }
        
        boolean isHighlighted = highlightingManipulator != null && highlightingManipulator.getHighlightedNode() == theNode;
        boolean isSelected = selectionManipulator != null && selectionManipulator.getNodeSelectionModel().isNodeSelected(theNode);
        boolean isDragging = draggingManipulator != null && draggingManipulator.getDraggedNode() == theNode;
        boolean notHighlightedBecauseOfSubGraph = theSubGraphHighlighter.isHighlightSubGraphs() && !theSubGraphHighlighter.doesSubGraphContain(theNode);
        
        if (notHighlightedBecauseOfSubGraph) {
            return notHighlightedTextColor;
        }
        else if (isHighlighted || isDragging || isSelected) {
            return textColor;
        }
        return textColor;
    }

    public boolean isInNode(JGraphPane graphPane, Node node, JPowerGraphPoint point, int size, double theScale) {
        JPowerGraphRectangle nodeScreenRectangle = new JPowerGraphRectangle(0, 0, 0, 0);
        getNodeScreenBounds(graphPane, node, size, theScale, nodeScreenRectangle);
        return nodeScreenRectangle.contains(point);
    }
    
    public void getNodeScreenBounds(JGraphPane graphPane, Node node, int size, double theScale, JPowerGraphRectangle nodeScreenRectangle) {
        JPowerGraphPoint nodePoint = graphPane.getScreenPointForNode(node);
        String label=node.getLabel();
        
        JPowerGraphGraphics g = graphPane.getJPowerGraphGraphics();
        
        if (size == NodePainter.LARGE){ 
            int width=20;
            int height=5;
            if (label!=null) {
                int stringWidth = stringWidth(g, label);
                int numlines = countLines(label);
                width += (stringWidth + (stringWidth/4));
                height += ((g.getAscent() + g.getDescent() + 10) * numlines);
            }
            else {
                width+=40;
                height+=20;
            }
            width = (int) Math.ceil(width * theScale);
            height = (int) Math.ceil(height * theScale);
            
            nodeScreenRectangle.x = nodePoint.x-width/2;
            nodeScreenRectangle.y = nodePoint.y-height/2;
            nodeScreenRectangle.width = width;
            nodeScreenRectangle.height = height;
        }
        else if (size == NodePainter.SMALL){
            int width=18;
            int height = 8;
            width = (int) Math.ceil(width * theScale);
            height = (int) Math.ceil(height * theScale);
            
            nodeScreenRectangle.x = nodePoint.x-width/2;
            nodeScreenRectangle.y = nodePoint.y-height/2;
            nodeScreenRectangle.width = width;
            nodeScreenRectangle.height = height;
        }
    }

    public JPowerGraphDimension getLegendItemSize(JGraphPane graphPane, String legendText) {
        JPowerGraphGraphics g = graphPane.getJPowerGraphGraphics();
        
        int padding = 2;
        int imageWidth = 18;
        int imageHeight = 8;
        int stringWidth = stringWidth(g, legendText);
        int width = imageWidth + stringWidth + (padding * 3);
        int height = Math.max(imageHeight, g.getAscent() + g.getDescent() + 4);
        return new JPowerGraphDimension(width, height);
    }

    public void paintLegendItem(JPowerGraphGraphics g, JPowerGraphPoint thePoint, String legendText) {
        int padding = 2;
        int imageWidth = 18;
        int imageHeight = 8;
        int imageX = thePoint.x;
        int imageY = thePoint.y;
        int textX = imageX + imageWidth + (padding * 3);
        int textY = imageY - 3; // SWING was + imageHeight and not - 3

        JPowerGraphColor bgColor = getBackgroundColor(new DefaultNode(), null, new DefaultSubGraphHighlighter());
        JPowerGraphColor boColor = getBorderColor(new DefaultNode(), null, new DefaultSubGraphHighlighter());
        JPowerGraphColor teColor = getTextColor(new DefaultNode(), null, new DefaultSubGraphHighlighter());

        JPowerGraphColor oldFGColor = g.getForeground();
        JPowerGraphColor oldBGColor = g.getBackground();
        if (shape == RECTANGLE) {
            g.setBackground(bgColor);
            g.fillRectangle(imageX, imageY, imageWidth, imageHeight);
            g.setForeground(boColor);
            g.drawRectangle(imageX, imageY, imageWidth, imageHeight);
        }
        else if (shape == ELLIPSE) {
            g.setBackground(bgColor);
            g.fillOval(imageX, imageY, imageWidth, imageHeight);
            g.setForeground(boColor);
            g.drawOval(imageX, imageY, imageWidth, imageHeight);
        }
        else if (shape == TRIANGLE) {
            int x1 = imageX;
            int y1 = imageY + imageHeight;
            int x2 = x1 + imageWidth / 2;
            int y2 = imageY - imageHeight / 2;
            int x3 = x1 + imageWidth;
            int y3 = y1;

            g.setBackground(bgColor);
            g.fillPolygon(new int[] { x1, y1, x2, y2, x3, y3 });
            g.setForeground(boColor);
            g.drawPolygon(new int[] { x1, y1, x2, y2, x3, y3 });
        }

        g.setBackground(oldBGColor);
        g.setForeground(teColor);
        g.drawString(legendText, textX, textY, 1);
        g.setForeground(oldFGColor);
        g.setBackground(oldBGColor);
    }

    public JPowerGraphColor getBorderColor() {
        return borderColor;
    }

    public JPowerGraphColor getBackgroundColor() {
        return backgroundColor;
    }

    private int stringWidth(JPowerGraphGraphics g, String s) {
        int max = Integer.MIN_VALUE;
        for (String line : getLines(s)) {
            max = Math.max(max, g.getStringWidth(line));
        }
        return max;
    }

    private int countLines(String s) {
        return getLines(s).size();
    }

    private ArrayList<String> getLines(String s) {
        ArrayList<String> result = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new StringReader(s));
        try {
            String line = "";
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            br.close();
        }
        catch (Exception e) {
        }
        return result;
    }
}
