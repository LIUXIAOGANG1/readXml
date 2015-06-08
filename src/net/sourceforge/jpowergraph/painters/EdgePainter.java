package net.sourceforge.jpowergraph.painters;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.SubGraphHighlighter;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;

/**
 * The painter for the edge.
 */
public interface EdgePainter <T extends Edge> {
    /**
     * Paints the supplied edge.
     *
     * @param graphPane             the graph pane
     * @param g                     the graphics
     * @param edge                  the edge to paint
     */
    void paintEdge(JGraphPane graphPane, JPowerGraphGraphics g, T edge, SubGraphHighlighter theSubGraphHighlighter);
    /**
     * Returns the distance of the point to the edge.
     *
     * @param graphPane             the graph pane
     * @param edge                  the edge
     * @param point                 the point
     * @return                      the distance of the point from the edge
     */
    double screenDistanceFromEdge(JGraphPane graphPane, T edge, JPowerGraphPoint point);
    /**
     * Returns the outer rectangle of the edge on screen.
     *
     * @param graphPane             the graph pane
     * @param edge                  the edge
     * @param edgeScreenRectangle   the rectangle receiving the edge's coordinates
     */
    void getEdgeScreenBounds(JGraphPane graphPane, T edge, JPowerGraphRectangle edgeScreenRectangle);
}
