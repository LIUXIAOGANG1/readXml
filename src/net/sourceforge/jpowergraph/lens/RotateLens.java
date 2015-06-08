package net.sourceforge.jpowergraph.lens;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.pane.JGraphPane;

/**
 * An implementation of the lens that allows rotation. This class has been initially implemented within
 * the <a href="http://www.touchgraph.com/">TouchGraph</a> library.
 */
public class RotateLens extends AbstractLens {
    /** The rotation angle. */
    protected double m_rotationAngle;
    /** The sine of the angle. */
    protected double m_sine;
    /** The cosine of the angle. */
    protected double m_cosine;

    /**
     * Creates a lens for rotation.
     */
    public RotateLens() {
        setRotationAngle(0.0);
    }
    /**
     * Returns the current rotation angle in degrees.
     *
     * @return                  the current rotation angle in degrees
     */
    public double getRotationAngle() {
        return m_rotationAngle;
    }
    /**
     * Sets the new rotation angle in degrees.
     *
     * @param rotationAngle     the new rotation angle in degrees
     */
    public void setRotationAngle(double rotationAngle) {
        m_rotationAngle=rotationAngle;
        double rotationAngleRadian=m_rotationAngle*Math.PI/180.0;
        m_sine=Math.sin(rotationAngleRadian);
        m_cosine=Math.cos(rotationAngleRadian);
        fireLensUpdated();
    }
    /**
     * Applies the lens to the point and modifies it according to the lens equations.
     *
     * @param point             the point that will be modified
     */
    public void applyLens(JGraphPane theJGraphPane, Point2D point) {
        double newX=point.getX()*m_cosine+point.getY()*m_sine;
        double newY=-point.getX()*m_sine+point.getY()*m_cosine;
        point.setLocation(newX,newY);
    }
    /**
     * Undoes the lens effect on the point.
     *
     * @param point             the point that will be modified
     */
    public void undoLens(JGraphPane theJGraphPane, Point2D point) {
        double newX=point.getX()*m_cosine-point.getY()*m_sine;
        double newY=point.getX()*m_sine+point.getY()*m_cosine;
        point.setLocation(newX,newY);
    }
}