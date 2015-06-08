package net.sourceforge.jpowergraph.lens;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.pane.JGraphPane;

public class TooltipLens extends AbstractLens {
    protected boolean showToolTips;

    public TooltipLens() {
        setShowToolTips(true);
    }

    public boolean isShowToolTips() {
        return showToolTips;
    }

    public void setShowToolTips(boolean theShowToolTips) {
        showToolTips=theShowToolTips;
        fireLensUpdated();
    }

    public void applyLens(JGraphPane theJGraphPane, Point2D point) {
    }

    public void undoLens(JGraphPane theJGraphPane, Point2D point) {
    }
}