package net.sourceforge.jpowergraph.manipulator.selection;

import java.util.Collection;

import net.sourceforge.jpowergraph.Node;

/**
 * Represents a selection of nodes.
 */
public interface NodeSelectionModel {
    /**
     * Adds a node to the selection.
     *
     * @param node                          the node to add to the selection
     */
    void addNode(Node node);
    /**
     * Adds a collection of nodes to the selection.
     *
     * @param nodes                         the collection of nodes to add to the selection
     */
    void addNodes(Collection <Node> nodes);
    /**
     * Removes a node from the selection.
     *
     * @param node                          the node to remove from the selection
     */
    void removeNode(Node node);
    /**
     * Removes a collection of nodes from the selection.
     *
     * @param nodes                         the collection of nodes to remove from the selection
     */
    void removeNodes(Collection <Node> nodes);
    /**
     * Clears the selection.
     */
    void clear();
    /**
     * Returns the selection.
     *
     * @return                              the selection
     */
    Collection <Node> getSelectedNodes();
    /**
     * Checks whteher the node has been selected.
     *
     * @param node                          the node that is checked
     * @return                              <code>true</code> if the node has been selected
     */
    boolean isNodeSelected(Node node);
    /**
     * Adds a listener to the selection.
     *
     * @param listener                      the listener to add to the selection
     */
    void addNodeSelectionListener(NodeSelectionListener listener);
    /**
     * Removes a listener from the selection.
     *
     * @param listener                      the listener to add to the selection
     */
    void removeNodeSelectionListener(NodeSelectionListener listener);
}
