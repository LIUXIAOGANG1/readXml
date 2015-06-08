package net.sourceforge.jpowergraph;

import java.util.Collection;
import java.util.List;

/**
 * Models a graph.
 */
public interface Graph {
    /**
     * Adds a listener to the graph.
     *
     * @param listener              the listener added to the graph
     */
    void addGraphListener(GraphListener listener);
    /**
     * Removes a listener from the graph.
     *
     * @param listener              the listener removed from the graph
     */
    void removeGraphListener(GraphListener listener);
    /**
     * Notifies the graph that its layout has been updated.
     */
    void notifyLayoutUpdated();
    /**
     * Notifies the graph that its nodes have been updated, but that no node has changed position.
     */
    void notifyUpdated();
    /**
     * Returns a list the nodes in the graph. The order of the list reflects the Z-order of nodes in the graph.
     * Elements with lower indices are lower in the Z-order.
     *
     * @return                      the list of the nodes in the graph
     */
    List <Node> getVisibleNodes();
    /**
     * Returns a collection of the edges in the graph. The order of the list reflects the Z-order of nodes in the graph.
     *
     * @return                      the list of the edges in the graph
     */
    List <Edge> getVisibleEdges();
    List <Node> getAllNodes();
    List <Edge> getAllEdges();
    void setNodeFilter(NodeFilter theNodeFilter);
    NodeFilter getNodeFilter();
    void addElements(Collection<Node> nodes, Collection<Edge> edges);
    void deleteElements(Collection<Node> nodes, Collection<Edge> edges);
    void mergeElements(Collection<Node> nodes, Collection<Edge> edges);
    void clear();
}
