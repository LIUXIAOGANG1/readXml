package net.sourceforge.jpowergraph;

/**
 * The edge of the graph.
 */
public interface Edge {
    /**
     * Returns the node from which this edge points.
     *
     * @return                       the node from which this edge points
     */
    Node getFrom();
    /**
     * Returns the node from which to edge points.
     *
     * @return                       the node to which this edge points
     */
    Node getTo();
    /**
     * Returns the label of this edge.
     *
     * @return                      the label of the node
     */
    String getLabels();
    /**
     * Returns the length of this edge.
     *
     * @return                      the ledge of the edge
     */
    double getLength();
}
