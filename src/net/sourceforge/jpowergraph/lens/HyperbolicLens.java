package net.sourceforge.jpowergraph.lens;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.pane.JGraphPane;

/**
 * An implementation of the lens that provides hyperbolic view. This class has been initially implemented within
 * the <a href="http://www.touchgraph.com/">TouchGraph</a> library.
 */
public class HyperbolicLens extends AbstractLens {
    /** The distortion factor of the hyperbolic lens. */
    protected double m_distortionFactor;
    /** The table of the values of the inverse hyperbolic function. */
    protected double[] m_inverseHyperbolicFunction=new double[200];

    /**
     * Creates a lens for hyperbolic view.
     */
    public HyperbolicLens() {
        setDistortionFactor(0.0);
    }
    /**
     * Returns the current distortion factor.
     *
     * @return                  the current distortion factory
     */
    public double getDistortionFactor() {
        return m_distortionFactor;
    }
    /**
     * Sets the new distortion factor.
     *
     * @param distortionFactor  the new distortion factor
     */
    public void setDistortionFactor(double distortionFactor) {
        m_distortionFactor=distortionFactor;
        for (int i=0;i<m_inverseHyperbolicFunction.length;i++)
            m_inverseHyperbolicFunction[i]=hyperDistance(10.0*i);
        fireLensUpdated();
    }
    /**
     * Computes the raw hyperbolic distance.
     *
     * @param distance          the distance
     * @return                  the raw hyperbolic distance
     */
    protected double rawHyperDistance(double distance) {
        return Math.log(distance/(Math.pow(1.5,(70-m_distortionFactor)/40)*80) +1);
    }
    /**
     * Computes the hyperbolic distance. Points that are more than 250 units away from the center stay fixed.
     *
     * @param distance          the distance
     * @return                  the raw hyperbolic distance
     */
    protected double hyperDistance(double distance) {
        return rawHyperDistance(distance)/rawHyperDistance(250.0)*250.0*m_distortionFactor/100.0+distance*(100.0-m_distortionFactor)/100.0;
    }
    /**
     * Computes the inverse of the hyperbolic distance.
     *
     * @param distance          the distance
     * @return                  the inverse hyperbolic distance
     */
    protected double invHyperDistance(double distance) {
        int i;
        if (m_inverseHyperbolicFunction[199]<distance)
            i=199;
        else
            i=findIndex(0,199,distance);
        double x2=m_inverseHyperbolicFunction[i];
        double x1=m_inverseHyperbolicFunction[i-1];
        double j=(distance-x1)/(x2-x1);
        return (i+j-1)*10.0;
    }
    /**
     * Locates the index of the element in the inverse array using the binary search algorithm.
     *
     * @param min               the minimum index
     * @param max               the maximum index
     * @param distance          the value being sought
     * @return                  the index in the array
     */
    protected int findIndex(int min,int max,double distance) {
        int mid=(min+max)/2;
        if (m_inverseHyperbolicFunction[mid]<distance){
            if (max-mid==1){
                return max;
            }
            return findIndex(mid,max,distance);
        }
        if (mid-min==1){
           return mid;
        }
        return findIndex(min,mid,distance);
    }
    /**
     * Applies the lens to the point and modifies it according to the lens equations.
     *
     * @param point             the point that will be modified
     */
    public void applyLens(JGraphPane theJGraphPane, Point2D point) {
        double x=point.getX();
        double y=point.getY();
        if (x!=0.0 || y!=0.0) {
            double distance=Math.sqrt(x*x+y*y);
            point.setLocation(x/distance*hyperDistance(distance),y/distance*hyperDistance(distance));
        }
    }
    /**
     * Undoes the lens effect on the point.
     *
     * @param point             the point that will be modified
     */
    public void undoLens(JGraphPane theJGraphPane, Point2D point) {
        double x=point.getX();
        double y=point.getY();
        if (x!=0.0 || y!=0.0) {
            double distance=Math.sqrt(x*x+y*y);
            point.setLocation(x/distance*invHyperDistance(distance),y/distance*invHyperDistance(distance));
        }
    }
}