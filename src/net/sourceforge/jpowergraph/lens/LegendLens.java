package net.sourceforge.jpowergraph.lens;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.pane.JGraphPane;

public class LegendLens extends AbstractLens {
    protected boolean showLegend;

    public LegendLens() {
        setShowLegend(true);
    }

    public boolean isShowLegend() {
        return showLegend;
    }

    public void setShowLegend(boolean theShowLegend) {
        showLegend = theShowLegend;
        fireLensUpdated();
    }

    public void applyLens(JGraphPane theJGraphPane, Point2D point) {
    }

    public void undoLens(JGraphPane theJGraphPane, Point2D point) {
    }
}