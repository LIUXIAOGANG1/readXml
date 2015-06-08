package net.sourceforge.jpowergraph.painters.node;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jpowergraph.ClusterNode;
import net.sourceforge.jpowergraph.Edge;
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

/**
 * The painter drawing the node as a rectangle.
 */
public class ClusterNodePainter implements NodePainter <ClusterNode> {
    
    private JPowerGraphColor backgroundColor;
    private JPowerGraphColor borderColor;
    private JPowerGraphColor textColor;

    private JPowerGraphColor notHighlightedBackgroundColor;
    private JPowerGraphColor notHighlightedBorderColor;
    private JPowerGraphColor notHighlightedTextColor;
    
    private ShapeNodePainter nodePainter;
    private boolean showNodes;
    private RowImageMap rowImageMap;
    private DefaultNode dummyNode;
    
    public ClusterNodePainter(int theShape, JPowerGraphColor theBackgroundColor, JPowerGraphColor theBorderColor, JPowerGraphColor theTextColor, boolean theShowNodes, RowImageMap theRowImageMap) {
        this.nodePainter = new ShapeNodePainter(theShape, theBackgroundColor, theBorderColor, theTextColor);
        
        this.backgroundColor = new JPowerGraphColor(246, 246, 246);
        this.borderColor = new JPowerGraphColor(197, 197, 197);
        this.textColor = JPowerGraphColor.BLACK;
        
        this.notHighlightedBackgroundColor = new JPowerGraphColor(246, 246, 246);
        this.notHighlightedBorderColor = new JPowerGraphColor(197, 197, 197);
        this.notHighlightedTextColor = new JPowerGraphColor(197, 197, 197);
        
        this.showNodes = theShowNodes;
        this.rowImageMap = theRowImageMap;
        
        this.dummyNode = new DefaultNode();
    }

    /**
     * Paints the supplied node.
     * 
     * @param graphPane
     *            the graph pane
     * @param g
     *            the graphics
     * @param node
     *            the node to paint
     */
    public void paintNode(JGraphPane graphPane, JPowerGraphGraphics g, ClusterNode node, int size, SubGraphHighlighter theSubGraphHighlighter) {
        JPowerGraphPoint nodePoint = graphPane.getScreenPointForNode(node);
        paintNode(graphPane, g, node, size, theSubGraphHighlighter, nodePoint);
    }

    public void paintNode(JGraphPane graphPane, JPowerGraphGraphics g, ClusterNode node, int size, SubGraphHighlighter theSubGraphHighlighter, JPowerGraphPoint thePoint) {
        paintNode(graphPane, g, node, size, theSubGraphHighlighter, thePoint, 1);
    }
    
    public void paintNode(JGraphPane graphPane, JPowerGraphGraphics g, ClusterNode node, int size, SubGraphHighlighter theSubGraphHighlighter, JPowerGraphPoint thePoint, double theScale) {
        int numInstances = node.size();

        int leftRightPad = 4;
        int hPadBetweenInstances = (int) (2 * getInstanceScale(numInstances));
        int vPadBetweenInstances = (int) (10 * getInstanceScale(numInstances)) - 1;
        
        JPowerGraphRectangle dummyRectangle = new JPowerGraphRectangle(0, 0, 0, 0);
        nodePainter.getNodeScreenBounds(graphPane, dummyNode, ShapeNodePainter.SMALL, getInstanceScale(numInstances), dummyRectangle);
        dummyRectangle.width += 2;
        dummyRectangle.height += 2;
        
        if (theSubGraphHighlighter != null && theSubGraphHighlighter.isHighlightSubGraphs() && theSubGraphHighlighter.doesSubGraphContain(node)){
            List <Node> nodes = new ArrayList <Node>();
            nodes.add(dummyNode);
            List <Edge> edges = new ArrayList <Edge>();
            theSubGraphHighlighter.getSubGraph().addElements(nodes, edges);
        }
        
        int numOuterCircles = countOuterCircles(numInstances);
        int numRows = (numOuterCircles * 2) + 1;
        int[] rowSizes = getRowSizes(numInstances, numRows);
        int numCols = Integer.MIN_VALUE;
        for (int i = 0; i < rowSizes.length; i++){
            numCols = Math.max(numCols, rowSizes[i]);
        }
            
        int width = 0;
        int height = 0;
        if (showNodes){
            width = (dummyRectangle.width * numCols) + (hPadBetweenInstances * numCols) + (leftRightPad * 2);
            height = (dummyRectangle.height  * numRows) + (vPadBetweenInstances * numRows - 1);
        }
        else{
            width=20;
            height=5;
            int stringWidth = stringWidth(g, "" + numInstances);
            int numlines = countLines("" + numInstances);
            width += (stringWidth + (stringWidth/4));
            height += ((g.getAscent() + g.getDescent() + 10) * numlines);
            width = (int) Math.ceil(width * theScale);
            height = (int) Math.ceil(height * theScale);
        }
       
        int radius = Math.max(width, height);
        
        JPowerGraphColor oldBGColor = g.getBackground();
        JPowerGraphColor oldFGColor = g.getForeground();
        g.setBackground(getBackgroundColor(node, graphPane, theSubGraphHighlighter));
        g.fillOval(thePoint.x - radius / 2, thePoint.y - radius / 2, radius, radius);

        g.setForeground(getBorderColor(node, graphPane, theSubGraphHighlighter));
        g.drawOval(thePoint.x - radius / 2, thePoint.y - radius / 2, radius, radius);

        if (showNodes){
            JPowerGraphGraphics subG = g.getSubJPowerGraphGraphics(new JPowerGraphDimension(dummyRectangle.width, dummyRectangle.height));
            double scale = getInstanceScale(numInstances);
            
            subG.setBackground(g.getBackground());
            subG.setForeground(g.getForeground());
            subG.setAntialias(g.getAntialias());
            subG.fillRectangle(0, 0, dummyRectangle.width, dummyRectangle.height);
            nodePainter.paintNode(graphPane, subG, dummyNode, ShapeNodePainter.SMALL, theSubGraphHighlighter, new JPowerGraphPoint(dummyRectangle.width/2, dummyRectangle.height/2), scale);
            JPowerGraphColor nodeColor = nodePainter.getBackgroundColor(dummyNode, graphPane, theSubGraphHighlighter);
            
            for (int i = 0; i < rowSizes.length; i++) {
                int rowIndex = (((numRows - 1) / 2) - i) * -1;
                int normalisedRowIndex = rowIndex;
                if (normalisedRowIndex < 0){
                    normalisedRowIndex *= -1;
                }
    
                int rowdx = 0;
                
                int numFullNodes = 0;
                int numHalfNodes = 0;
                int numGaps = 0;
                if (i % 2 != 0) {
                    numFullNodes = (rowSizes[i] - 2);
                    numHalfNodes = 1;
                    numGaps = (rowSizes[i] - 1);
                }
                else{
                    numFullNodes = (rowSizes[i] - 1);
                    numHalfNodes = 0;
                    numGaps = (rowSizes[i] - 1);
                }
                rowdx = (numFullNodes * dummyRectangle.width) + (numHalfNodes * dummyRectangle.width) + (numGaps * hPadBetweenInstances) ;
                rowdx = rowdx / 2;
                
                JPowerGraphGraphics rowG = rowImageMap.retrieve(scale, rowSizes[i], g.getBackground(), nodeColor);
                if (rowG == null){
                    int rowWidth = (dummyRectangle.width + hPadBetweenInstances) * rowSizes[i] + 1;
                    rowG = g.getSubJPowerGraphGraphics(new JPowerGraphDimension(rowWidth, dummyRectangle.height));
                    rowG.setBackground(g.getBackground());
                    rowG.setForeground(g.getForeground());
                    rowG.setAntialias(g.getAntialias());
                    rowG.fillRectangle(0, 0, rowWidth, dummyRectangle.height);
                    for (int j = 0; j < rowSizes[i]; j++) {
                        int thisDx = (dummyRectangle.width + hPadBetweenInstances) * j;
                        
                        JPowerGraphPoint thisPoint = new JPowerGraphPoint(thisDx + dummyRectangle.width/2, dummyRectangle.height/2);
                        rowG.drawSubJPowerGraph(subG, thisPoint.x - dummyRectangle.width/2, thisPoint.y - dummyRectangle.height/2);
                    }
                    rowImageMap.register(scale, rowSizes[i], rowG, g.getBackground(), nodeColor);
                }
    
                int rowdy = rowIndex * (dummyRectangle.height + vPadBetweenInstances);
                JPowerGraphPoint thisPoint = new JPowerGraphPoint(thePoint.x - rowdx, thePoint.y + rowdy);
                g.drawSubJPowerGraph(rowG, thisPoint.x - dummyRectangle.width/2, thisPoint.y - dummyRectangle.height/2);
            }
            subG.dispose();
        }
        else{
            int stringWidth = stringWidth(g, "" + numInstances);
            int numlines = countLines("" + numInstances);
            int textX=(thePoint.x-stringWidth/2) + 1;
            int textY=thePoint.y - (7 * numlines);
            
            g.storeFont();
            g.setFontFromJGraphPane(graphPane);
            g.setForeground(getTextColor(node, graphPane, theSubGraphHighlighter));
            ArrayList<String> lines = getLines("" + numInstances);
            for (int i = 0; i < lines.size(); i++) {
                int offset = (g.getAscent() + g.getDescent() + 2) * i;
                g.drawString(lines.get(i), textX, textY + offset, lines.size());
            }
            g.restoreFont();
        }
        
        if (node.showLabel() && !node.getLabel().equals("")){
            int stringWidth = stringWidth(g, node.getLabel());
            int stringHeight = g.getAscent() + g.getDescent();
            int numlines = countLines(node.getLabel());
            int textX = (thePoint.x-stringWidth/2) + 1;
            int textY = thePoint.y + (radius/2) + (stringHeight/2);
            
            int ovalX = textX - 8;
            int ovalY = textY - 4;
            int ovalWidth = stringWidth + 13;
            int ovalHeight = ((stringHeight + 2)  * numlines) + 8;
            
            g.fillRoundRectangle(ovalX, ovalY, ovalWidth, ovalHeight, 10, 10);
            g.drawRoundRectangle(ovalX, ovalY, ovalWidth, ovalHeight, 10, 10);
            
            g.storeFont();
            g.setFontFromJGraphPane(graphPane);
            g.setForeground(getTextColor(node, graphPane, theSubGraphHighlighter));
            ArrayList<String> lines = getLines(node.getLabel());
            for (int i = 0; i < lines.size(); i++) {
                int offset = (g.getAscent() + g.getDescent() + 2) * i;
                g.drawString(lines.get(i), textX, textY + offset, lines.size());
            }
            g.restoreFont();
        }
        
        g.setBackground(oldBGColor);
        g.setForeground(oldFGColor);
    }

    private int[] getRowSizes(int numInstances, int numRows) {
        int[] result = new int[numRows];
        for (int i = 0; i < result.length; i++) {
            int rowIndex = ((numRows - 1) / 2) - i;
            if (rowIndex < 0) {
                rowIndex = rowIndex * -1;
            }
            result[i] = numRows - rowIndex;
        }

        int numCurrentNodes = 0;
        for (int i = 0; i < result.length; i++) {
            numCurrentNodes += result[i];
        }

        int numOverNodes = numCurrentNodes - numInstances;
        while (numOverNodes > 0) {
            while((result[result.length - 1] > 0 || result[0] > 0) && numOverNodes != 0){
                if (result[result.length - 1] > 0 && numOverNodes != 0){
                    result[result.length - 1] -= 1;
                    numOverNodes -= 1;
                }
                if (result[0] > 0 && numOverNodes != 0){
                    result[0] -= 1;
                    numOverNodes -= 1;
                }
            }
            
            for (int i = result.length - 2; i >= 0; i--) {
                if (numOverNodes != 0 && result[i] > 0) {
                    result[i] -= 1;
                    numOverNodes -= 1;
                }
            }
        }

        return result;
    }

    private int countOuterCircles(int numInstances) {
        int numOuterCircles = 0;

        int temp = numInstances - 1;
        int nextCircle = 1;
        while (temp > 0) {
            numOuterCircles++;
            temp -= nextCircle * 6;
            nextCircle++;
        }

        return numOuterCircles;
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

    /**
     * Checks whether given point is inside the node.
     * 
     * @param graphPane
     *            the graph pane
     * @param node
     *            the node
     * @param point
     *            the point
     * @return <code>true</code> if the point is in the node
     */
    public boolean isInNode(JGraphPane graphPane, ClusterNode node, JPowerGraphPoint point, int size, double theScale) {
        JPowerGraphRectangle nodeScreenRectangle = new JPowerGraphRectangle(0, 0, 0, 0);
        getNodeScreenBounds(graphPane, node, size, theScale, nodeScreenRectangle);
        
        JPowerGraphRectangle labelScreenRectangle = new JPowerGraphRectangle(0, 0, 0, 0);
        getLabelScreenBounds(graphPane, node, nodeScreenRectangle, labelScreenRectangle);
        
        return nodeScreenRectangle.contains(point) || labelScreenRectangle.contains(point);
    }


    /**
     * Returns the outer rectangle of the node on screen.
     * 
     * @param graphPane
     *            the graph pane
     * @param node
     *            the node
     * @param nodeScreenRectangle
     *            the rectangle receiving the node's coordinates
     */
    public void getNodeScreenBounds(JGraphPane graphPane, ClusterNode node, int size, double theScale, JPowerGraphRectangle nodeScreenRectangle) {
        JPowerGraphPoint nodePoint = graphPane.getScreenPointForNode(node);

        int numInstances = node.size();

        int leftRightPad = 4;
        int hPadBetweenInstances = (int) (2 * getInstanceScale(numInstances));
        
        JPowerGraphRectangle dummyRectangle = new JPowerGraphRectangle(0, 0, 0, 0);
        nodePainter.getNodeScreenBounds(graphPane, dummyNode, ShapeNodePainter.SMALL, getInstanceScale(numInstances), dummyRectangle);
        dummyRectangle.width += 2;
        dummyRectangle.height += 2;
        
        int numOuterCircles = countOuterCircles(numInstances);
        int numRows = (numOuterCircles * 2) + 1;
        int[] rowSizes = getRowSizes(numInstances, numRows);
        int numCols = Integer.MIN_VALUE;
        for (int i = 0; i < rowSizes.length; i++){
            numCols = Math.max(numCols, rowSizes[i]);
        }
        String label = "" + numInstances;
            
        int width = 0;
        if (showNodes){
            width = (dummyRectangle.width * numCols) + (hPadBetweenInstances * (numCols - 1)) + (leftRightPad * 2);
        }
        else{
            width=20;
            int stringWidth = stringWidth(graphPane.getJPowerGraphGraphics(), label);
            width += (stringWidth + (stringWidth/4));
            width = (int) Math.ceil(width * theScale);
        }

        nodeScreenRectangle.x = nodePoint.x - width / 2;
        nodeScreenRectangle.y = nodePoint.y - width / 2;
        nodeScreenRectangle.width = width;
        nodeScreenRectangle.height = width;
    }
    
    private void getLabelScreenBounds(JGraphPane graphPane, ClusterNode node, JPowerGraphRectangle nodeScreenRectangle, JPowerGraphRectangle labelScreenRectangle) {
        if (node.showLabel() && !node.getLabel().equals("")){
            JPowerGraphPoint nodePoint = graphPane.getScreenPointForNode(node);
            int stringWidth = stringWidth(graphPane.getJPowerGraphGraphics(), node.getLabel());
            int stringHeight = graphPane.getJPowerGraphGraphics().getAscent() + graphPane.getJPowerGraphGraphics().getDescent();
            int numlines = countLines(node.getLabel());
            int textX = (nodePoint.x-stringWidth/2) + 1;
            int textY = nodePoint.y + (nodeScreenRectangle.width/2) + (stringHeight/2);
            
            labelScreenRectangle.x = textX - 8;
            labelScreenRectangle.y = textY - 4;
            labelScreenRectangle.width = stringWidth + 10;
            labelScreenRectangle.height = (stringHeight * numlines) + 10;
        }
    }

    private double getInstanceScale(int numInstances) {
        double scale = 0.5;
        if (numInstances > 100){
            scale -= 0.1;
        }
        if (numInstances > 200){
            scale -= 0.1;
        }
        if (numInstances > 400){
            scale -= 0.1;
        }
        if (numInstances > 800){
            scale -= 0.1;
        }
        return scale;
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

    public void paintLegendItem(JPowerGraphGraphics g, JPowerGraphPoint thePoint, String legendText) {
        int padding = 2;
        int imageWidth = 8;
        int imageHeight = 8;
        int imageX = thePoint.x + (padding * 2);
        int imageY = thePoint.y;
        int textX = imageX + imageWidth + (padding * 6);
        int textY = imageY - 3;

        JPowerGraphColor bgColor = getBackgroundColor(new DefaultNode(), null, new DefaultSubGraphHighlighter());
        JPowerGraphColor boColor = getBorderColor(new DefaultNode(), null, new DefaultSubGraphHighlighter());
        JPowerGraphColor teColor = getTextColor(new DefaultNode(), null, new DefaultSubGraphHighlighter());

        JPowerGraphColor oldFGColor = g.getForeground();
        JPowerGraphColor oldBGColor = g.getBackground();

        g.setBackground(bgColor);
        g.fillOval(imageX, imageY, imageWidth, imageHeight);
        g.setForeground(boColor);
        g.drawOval(imageX, imageY, imageWidth, imageHeight);
        
        g.setBackground(nodePainter.getBackgroundColor());
        g.fillOval(imageX + imageWidth/2 - 1, imageY + imageHeight/2 - 1, 1, 1);
        g.fillOval(imageX + imageWidth/2 - 1, imageY + imageHeight/2 + 1, 1, 1);
        g.fillOval(imageX + imageWidth/2 + 1, imageY + imageHeight/2 - 1, 1, 1);
        g.fillOval(imageX + imageWidth/2 + 1, imageY + imageHeight/2 + 1, 1, 1);
        g.fillOval(imageX + imageWidth/2 + 1, imageY + imageHeight/2, 1, 1);
        g.fillOval(imageX + imageWidth/2 - 1, imageY + imageHeight/2, 1, 1);

        g.setBackground(oldBGColor);
        g.setForeground(teColor);
        g.drawString(legendText, textX, textY, 1);
        g.setForeground(oldFGColor);
        g.setBackground(oldBGColor);
    }

    public JPowerGraphDimension getLegendItemSize(JGraphPane graphPane, String legendText) {
        int padding = 2;
        int imageWidth = 18;
        int imageHeight = 8;
        int stringWidth = stringWidth(graphPane.getJPowerGraphGraphics(), legendText);
        int width = imageWidth + stringWidth + (padding * 3);
        int height = Math.max(imageHeight, graphPane.getJPowerGraphGraphics().getAscent() + graphPane.getJPowerGraphGraphics().getDescent() + 4);
        return new JPowerGraphDimension(width, height);
    }

    public JPowerGraphColor getBorderColor() {
        return borderColor;
    }

    public JPowerGraphColor getBackgroundColor() {
        return backgroundColor;
    }
}
