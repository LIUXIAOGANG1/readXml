package net.sourceforge.jpowergraph;

import java.util.List;

/**
 * The node in the graph.
 */
public interface Node {
    /**
     * Returns the list of edges from the node.
     *
     * @return                              the list of edges from the node
     */
    List <Edge> getEdgesFrom();
    /**
     * Returns the list of edges to the node.
     *
     * @return                              the list of edges to the node
     */
    List <Edge> getEdgesTo();
    /**
     * Set the location of this node.
     *
     * @param x                             the X coordination
     * @param y                             the Y coordination
     */
    void setLocation(double x,double y);
    /**
     * Returns the X coordinate of this node.
     *
     * @return                              the X coordinate
     */
    double getX();
    /**
     * Returns the Y coordinate of this node.
     *
     * @return                              the Y coordinate
     */
    double getY();
    /**
     * The repulstion factor (specifies how much does this node repulses other nodes).
     *
     * @return                              the repulsion factor of the node
     */
    double getRepulsion();
    /**
     * Return the label of this node.
     *
     * @return                               the label of this node
     */
    String getLabel();
    /**
     * Returns <code>true</code> if this node is fixed (in place).
     *
     * @return                              <code>true</code> if this node is fixed
     */
    boolean isFixed();
    /**
     * Makes this node fixed.
     *
     * @param fixed                         <code>true</code> if this node is fixed
     */
    void setFixed(boolean fixed);
    /**
     * Returns a text description of the node type
     *
     * @return String                        
     */
    String getNodeType();
    boolean visibleOnlyIfHasOutgoingEdge();
    void setVisibleOnlyIfHasOutgoingEdge(boolean isVisibleOnlyIfHasOutgoingEdge);
    boolean visibleOnlyIfHasIncomingEdge();
    void setVisibleOnlyIfHasIncomingEdge(boolean isVisibleOnlyIfHasIncomingEdge);
}
