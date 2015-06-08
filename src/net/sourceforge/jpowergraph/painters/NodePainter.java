package net.sourceforge.jpowergraph.painters;

import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.SubGraphHighlighter;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphDimension;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;

/**
 * The painter for the node.
 */
public interface NodePainter <T extends Node> {

    public static final int SMALL = 0;

    public static final int LARGE = 1;

    /**
     * Paints the supplied node.
     * 
     * @param graphPane             the graph pane
     * @param g                     the graphics
     * @param node                  the node to paint
     */
    void paintNode(JGraphPane graphPane, JPowerGraphGraphics g, T node, int size, SubGraphHighlighter theSubGraphHighlighter);

    /**
     * Paints the supplied node at the specified point.
     * 
     * @param graphPane             the graph pane
     * @param g                     the graphics
     * @param node                  the node to paint
     */
    void paintNode(JGraphPane graphPane, JPowerGraphGraphics g, T node, int size, SubGraphHighlighter theSubGraphHighlighter, JPowerGraphPoint thePoint);

    /**
     * Paints the supplied node at the specified point at the specified scale.
     * 
    * @param graphPane             the graph pane
    * @param g                     the graphics
    * @param node                  the node to paint
     */
    void paintNode(JGraphPane graphPane, JPowerGraphGraphics g, T node, int size, SubGraphHighlighter theSubGraphHighlighter, JPowerGraphPoint thePoint, double theScale);

    /**
     * Checks whether given point is inside the node.
     * 
     * @param graphPane             the graph pane
     * @param node                  the node
     * @param point                 the point
     * @return <code>true</code> if the point is in the node
     */
    boolean isInNode(JGraphPane graphPane, T node, JPowerGraphPoint point, int size, double scale);

    /**
     * Returns the outer rectangle of the node on screen.
     * 
     * @param graphPane             the graph pane
     * @param node                  the node
     * @param nodeScreenRectangle   the rectangle receiving the node's coordinates
     */
    void getNodeScreenBounds(JGraphPane graphPane, T node, int size, double scale, JPowerGraphRectangle nodeScreenRectangle);

    /**
     * Paints the specified legend item
     * 
     * @param ThePoint the point to draw the legend item at
     * @param legendText The text for this legend item
     */
    void paintLegendItem(JPowerGraphGraphics g, JPowerGraphPoint thePoint, String legendText);

    /**
     * Returns the dimensions of this legend item
     */
    JPowerGraphDimension getLegendItemSize(JGraphPane graphPane, String legendText);
}
