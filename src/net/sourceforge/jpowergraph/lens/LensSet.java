package net.sourceforge.jpowergraph.lens;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jpowergraph.pane.JGraphPane;

/**
 * A collection of lenses, where each lens is a function that warps 2D space.
 * This class has been inspired by the <a href="http://www.touchgraph.com/">TouchGraph</a> library.
 */
public class LensSet extends AbstractLens implements LensListener {
    /** The list of lenses. */
    protected List <Lens> m_lenses;

    /**
     * Creates the lens set.
     */
    public LensSet() {
        m_lenses=new ArrayList <Lens> ();
    }
    /**
     * Adds the lens to the set.
     *
     * @param lens                  the lens added
     */
    public void addLens(Lens lens) {
        m_lenses.add(lens);
        lens.addLensListener(this);
    }
    
    public List <Lens> getLensOfType(Class theClass){
        List <Lens> result = new ArrayList <Lens> ();
        for (Iterator i = m_lenses.iterator(); i.hasNext();) {
            Lens l = (Lens) i.next();
            if (l.getClass().equals(theClass)){
                result.add(l);
            }
        }
        return result;
    }
    
    public Lens getFirstLensOfType(Class theClass) {
        List<Lens> lenses = getLensOfType(theClass);
        if (lenses.size() > 0){
            return lenses.get(0);
        }
        return null;
    }
    
    /**
     * Applies the lenses.
     *
     * @param point                 the point
     */
    public void applyLens(JGraphPane theJGraphPane, Point2D point) {
        for (int i=0;i<m_lenses.size();i++) {
            Lens lens= m_lenses.get(i);
            lens.applyLens(theJGraphPane, point);
        }
    }
    /**
     * Undoes the lenses.
     *
     * @param point                 the point
     */
    public void undoLens(JGraphPane theJGraphPane, Point2D point) {
        for (int i=m_lenses.size()-1;i>=0;i--) {
            Lens lens= m_lenses.get(i);
            lens.undoLens(theJGraphPane, point);
        }
    }
    /**
     * Called whenever the lens changes its parameters.
     *
     * @param lens                      the lens that changed its parameters
     */
    public void lensUpdated(Lens lens) {
        fireLensUpdated();
    }
}
