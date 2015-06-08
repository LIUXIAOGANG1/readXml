package net.sourceforge.jpowergraph.defaults;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;

/**
 * The default implementation of the edge.
 */
public class DefaultEdge implements Edge {
    /** The node from the edge. */
    protected Node m_from;
    /** The node to the edge. */
    protected Node m_to;

    /**
     * Creates an instance of this class.
     *
     * @param from                  the node from
     * @param to                    the node to
     */
    public DefaultEdge(Node from,Node to) {
        m_from=from;
        m_to=to;
    }
    /**
     * Returns the node from which this edge points.
     *
     * @return                       the node from which this edge points
     */
    public Node getFrom() {
        return m_from;
    }
    /**
     * Returns the node from which to edge points.
     *
     * @return                       the node to which this edge points
     */
    public Node getTo() {
        return m_to;
    }
    
    /**
     * Sets the node from which this edge points.
     *
     * @param                       the node from which this edge points
     */
    public void setFrom(Node theNode){
        m_from = theNode;
    }
    
    /**
     * Sets the node from which to edge points.
     *
     * @param                       the node to which this edge points
     */
    public void setTo(Node theNode){
        m_to = theNode;
    }
    
    /**
     * Returns the label of this node.
     *
     * @return                      the label of the node
     */
    public String getLabels() {
        return toString();
    }
    /**
     * Returns the length of this edge.
     *
     * @return                      the ledge of the edge
     */
    public double getLength() {
        return 40;
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof DefaultEdge)){
            return false;
        }
        DefaultEdge otherEdge = (DefaultEdge) obj;
        return (otherEdge.getFrom().equals(getFrom()) && otherEdge.getTo().equals(getTo())) ||
               (otherEdge.getTo().equals(getFrom()) && otherEdge.getFrom().equals(getTo()));
    }
}
