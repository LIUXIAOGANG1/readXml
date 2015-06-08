package net.sourceforge.jpowergraph.lens;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.pane.JGraphPane;

/**
 * An implementation of the lens that allows transaltion.
 */
public class TranslateLens extends AbstractLens {
    /** The translation along X axis. */
    protected double m_translateX;
    /** The translation along Y axis. */
    protected double m_translateY;

    /**
     * Creates a lens for zooming.
     */
    public TranslateLens() {
        setTranslate(0,0);
    }
    /**
     * Returns the translation along X axis.
     *
     * @return                  the translation along X axis
     */
    public double getTranslateX() {
        return m_translateX;
    }
    /**
     * Returns the translation along Y axis.
     *
     * @return                  the translation along Y axis
     */
    public double getTranslateY() {
        return m_translateY;
    }
    /**
     * Sets the new translation factory.
     *
     * @param translateX        translation along X axis
     * @param translateY        translation along Y axis
     */
    public void setTranslate(double translateX,double translateY) {
        m_translateX=translateX;
        m_translateY=translateY;
        fireLensUpdated();
    }
    /**
     * Applies the lens to the point and modifies it according to the lens equations.
     *
     * @param point             the point that will be modified
     */
    public void applyLens(JGraphPane theJGraphPane, Point2D point) {
        point.setLocation(point.getX()+m_translateX,point.getY()+m_translateY);
    }
    /**
     * Undoes the lens effect on the point.
     *
     * @param point             the point that will be modified
     */
    public void undoLens(JGraphPane theJGraphPane, Point2D point) {
        point.setLocation(point.getX()-m_translateX,point.getY()-m_translateY);
    }
}