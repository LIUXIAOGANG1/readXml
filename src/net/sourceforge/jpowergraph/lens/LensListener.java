package net.sourceforge.jpowergraph.lens;

import java.util.EventListener;

/**
 * A that gets notified when the lens changes its parameters.
 */
public interface LensListener extends EventListener {
    /**
     * Called whenever the lens changes its parameters.
     *
     * @param lens                      the lens that changed its parameters
     */
    void lensUpdated(Lens lens);
}