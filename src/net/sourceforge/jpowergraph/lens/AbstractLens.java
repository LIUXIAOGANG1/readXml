package net.sourceforge.jpowergraph.lens;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLens implements Lens {
    
    private List <LensListener> listeners;

    public AbstractLens() {
        listeners = new ArrayList <LensListener>();
    }

    public void addLensListener(LensListener listener) {
        listeners.add(listener);
    }

    public void removeLensListener(LensListener listener) {
        listeners.remove(listener);
    }
    
    public void fireLensUpdated() {
        for (LensListener listener : listeners){
            listener.lensUpdated(this);
        }
    }
}