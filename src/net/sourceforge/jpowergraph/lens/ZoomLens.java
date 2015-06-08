package net.sourceforge.jpowergraph.lens;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.pane.JGraphPane;

/**
 * An implementation of the lens that allows zooming. This class has been initially implemented within
 * the <a href="http://www.touchgraph.com/">TouchGraph</a> library.
 */
public class ZoomLens extends AbstractLens {
    /** The zoom factor. */
    protected double m_zoomFactor;

    /**
     * Creates a lens for zooming.
     */
    public ZoomLens() {
        setZoomFactor(1.0);
    }
    /**
     * Returns the current zoom factor.
     *
     * @return                  the current zoom factor
     */
    public double getZoomFactor() {
        return m_zoomFactor;
    }
    /**
     * Sets the new zoom factor.
     *
     * @param zoomFactor        the new zoom factor
     */
    public void setZoomFactor(double zoomFactor) {
        m_zoomFactor=zoomFactor;
        fireLensUpdated();
    }
    /**
     * Applies the lens to the point and modifies it according to the lens equations.
     *
     * @param point             the point that will be modified
     */
    public void applyLens(JGraphPane theJGraphPane, Point2D point) {
        point.setLocation(point.getX()*m_zoomFactor,point.getY()*m_zoomFactor);
    }
    /**
     * Undoes the lens effect on the point.
     *
     * @param point             the point that will be modified
     */
    public void undoLens(JGraphPane theJGraphPane, Point2D point) {
        point.setLocation(point.getX()/m_zoomFactor,point.getY()/m_zoomFactor);
    }
}