package net.sourceforge.jpowergraph.lens;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.pane.JGraphPane;

/**
 * A lens that warps 2D space. This class has been inspired by the <a href="http://www.touchgraph.com/">TouchGraph</a> library.
 */
public interface Lens {
    /**
     * Applies the lens to the point and modifies it according to the lens equations.
     *
     * @param point             the point that will be modified
     */
    void applyLens(JGraphPane theJGraphPane, Point2D point);
    /**
     * Undoes the lens effect on the point.
     *
     * @param point             the point that will be modified
     */
    void undoLens(JGraphPane theJGraphPane, Point2D point);
    /**
     * Adds a listener to the lens.
     *
     * @param listener          the listener to add
     */
    void addLensListener(LensListener listener);
    /**
     * Removes a listener from the lens.
     *
     * @param listener          the listener to remove
     */
    void removeLensListener(LensListener listener);
}