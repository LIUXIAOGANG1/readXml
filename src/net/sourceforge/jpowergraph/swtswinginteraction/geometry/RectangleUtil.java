package net.sourceforge.jpowergraph.swtswinginteraction.geometry;


/**
 * @author Mick Kerrigan
 *
 * Created on 29-Jul-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/common/src/net/sourceforge/jpowergraph/swtswinginteraction/geometry/RectangleUtil.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:35:40 $
 */
public class RectangleUtil {

    public static boolean contains(JPowerGraphRectangle r1, JPowerGraphRectangle r2) {
        int w = r2.width;
        int h = r2.height;
        if ((w | h | r1.width | r1.height) < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if any dimension is zero, tests below must return false...
        int x = r2.x;
        int y = r2.y;
        if (r1.x < x || r1.y < y) {
            return false;
        }
        w += x;
        r1.width += r1.x;
        if (r1.width <= r1.x) {
            // X+W overflowed or W was zero, return false if...
            // either original w or W was zero or
            // x+w did not overflow or
            // the overflowed x+w is smaller than the overflowed X+W
            if (w >= x || r1.width > w)
                return false;
        }
        else {
            // X+W did not overflow and W was not zero, return false if...
            // original w was zero or
            // x+w did not overflow and x+w is smaller than X+W
            if (w >= x && r1.width > w)
                return false;
        }
        h += y;
        r1.height += r1.y;
        if (r1.height <= r1.y) {
            if (h >= y || r1.height > h)
                return false;
        }
        else {
            if (h >= y && r1.height > h)
                return false;
        }
        return true;
    }
}
