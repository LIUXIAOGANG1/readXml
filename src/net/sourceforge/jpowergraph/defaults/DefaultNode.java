package net.sourceforge.jpowergraph.defaults;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;

/**
 * The default implementation of the node in the graph.
  */
public class DefaultNode implements Node {
    /** The list of edges from the node. */
    protected List <Edge> m_edgesFrom;
    /** The list of edges to the node. */
    protected List <Edge> m_edgesTo;
    /** Set to <code>true</code> if the node is fixed. */
    protected boolean m_fixed;
    /** The x location of the node. */
    protected double m_x;
    /** The y location of the node. */
    protected double m_y;
    private boolean visibleOnlyIfHasIncomingEdge = false;
    private boolean visibleOnlyIfHasOutgoingEdge = false;

    /**
     * Creates a node.
     */
    public DefaultNode() {
        m_fixed=false;
        m_edgesFrom=new ArrayList <Edge> ();
        m_edgesTo=new ArrayList <Edge> ();
    }
    /**
     * Returns the list of edges from the node.
     *
     * @return                              the list of edges from the node
     */
    public List <Edge> getEdgesFrom() {
        return m_edgesFrom;
    }
    /**
     * Returns the list of edges to the node.
     *
     * @return                              the list of edges to the node
     */
    public List <Edge> getEdgesTo() {
        return m_edgesTo;
    }
    /**
     * Set the location of this node.
     *
     * @param x                             the X coordination
     * @param y                             the Y coordination
     */
    public void setLocation(double x,double y) {
        m_x=x;
        m_y=y;
    }
    /**
     * Returns the X coordinate of this node.
     *
     * @return                              the X coordinate
     */
    public double getX() {
        return m_x;
    }
    /**
     * Returns the Y coordinate of this node.
     *
     * @return                              the Y coordinate
     */
    public double getY() {
        return m_y;
    }
    /**
     * The repulstion factor (specifies how much does this node repulses other nodes).
     *
     * @return                              the repulsion factor of the node
     */
    public double getRepulsion() {
        return 1.0;
    }
   /**
    * Return the label of this node.
    *
    * @return                               the label of this node
    */
    public String getLabel() {
        return toString();
    }
    /**
     * Returns <code>true</code> if this node is fixed (in place).
     *
     * @return                              <code>true</code> if this node is fixed
     */
    public boolean isFixed() {
        return m_fixed;
    }
    /**
     * Makes this node fixed.
     *
     * @param fixed                         <code>true</code> if this node is fixed
     */
    public void setFixed(boolean fixed) {
        m_fixed=fixed;
    }
    /**
     * Notifies the node that the edge has been added to the graph.
     *
     * @param edge                          the edge that was added
     */
    public void notifyEdgeAdded(Edge edge) {
        if (edge.getFrom()==this)
            m_edgesFrom.add(edge);
        else if (edge.getTo()==this)
            m_edgesTo.add(edge);
    }
    /**
     * Notifies the node that the edge has been removed from the graph.
     *
     * @param edge                          the edge that was removed
     */
    public void notifyEdgeRemoved(Edge edge) {
        if (edge.getFrom()==this)
            m_edgesFrom.remove(edge);
        else if (edge.getTo()==this)
            m_edgesTo.remove(edge);
    }
    public String getNodeType(){
        return "Default Node";
    }
    public boolean visibleOnlyIfHasIncomingEdge() {
        return visibleOnlyIfHasIncomingEdge ;
    }
    public boolean visibleOnlyIfHasOutgoingEdge() {
        return visibleOnlyIfHasOutgoingEdge ;
    }
    public void setVisibleOnlyIfHasIncomingEdge(boolean isVisibleOnlyIfHasIncomingEdge) {
        this.visibleOnlyIfHasIncomingEdge = isVisibleOnlyIfHasIncomingEdge;
    }
    public void setVisibleOnlyIfHasOutgoingEdge(boolean isVisibleOnlyIfHasOutgoingEdge) {
        this.visibleOnlyIfHasOutgoingEdge = isVisibleOnlyIfHasOutgoingEdge;
    }
}
