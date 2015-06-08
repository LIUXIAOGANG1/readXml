package net.sourceforge.jpowergraph.lens;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.painters.NodePainter;
import net.sourceforge.jpowergraph.pane.JGraphPane;

public class NodeSizeLens extends AbstractLens {
    protected int nodeSize;

    public NodeSizeLens() {
        setNodeSize(NodePainter.LARGE);
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public void setNodeSize(int theNodeSize) {
        nodeSize = theNodeSize;
        fireLensUpdated();
    }

    public void applyLens(JGraphPane theJGraphPane, Point2D point) {
        theJGraphPane.setNodeSize(nodeSize);
    }

    public void undoLens(JGraphPane theJGraphPane, Point2D point) {
        theJGraphPane.setNodeSize(nodeSize);
    }
}