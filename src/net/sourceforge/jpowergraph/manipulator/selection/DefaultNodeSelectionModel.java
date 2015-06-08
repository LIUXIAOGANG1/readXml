package net.sourceforge.jpowergraph.manipulator.selection;

import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.EventListenerList;

import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.GraphListener;
import net.sourceforge.jpowergraph.Node;

/**
 * A default implementation of the node selection model.
 */
public class DefaultNodeSelectionModel implements NodeSelectionModel {
    /** The list of registered listeners. */
    protected EventListenerList m_eventListenerList;
    /** The set of selected nodes. */
    protected Set <Node> m_selectedNodes;
    /** The graph being managed by this model. */
    protected Graph m_graph;

    /**
     * Creates an instance of this class.
     *
     * @param graph                         the graph
     */
    public DefaultNodeSelectionModel(Graph graph) {
        m_graph=graph;
        m_eventListenerList=new EventListenerList();
        m_selectedNodes=new HashSet <Node> ();
        m_graph.addGraphListener(new GraphHandler());
    }
    /**
     * Adds a node to the selection.
     *
     * @param node                          the node to add to the selection
     */
    public void addNode(Node node) {
        Set <Node> addedNode=new HashSet <Node> ();
        addedNode.add(node);
        addNodes(addedNode);
    }
    /**
     * Adds a collection of nodes to the selection.
     *
     * @param nodes                         the collection of nodes to add to the selection
     */
    public void addNodes(Collection <Node> nodes) {
        if (m_selectedNodes.addAll(nodes))
            fireNodesAddedToSelection(nodes);
    }
    /**
     * Removes a node from the selection.
     *
     * @param node                          the node to remove from the selection
     */
    public void removeNode(Node node) {
        Set <Node> removedNode=new HashSet <Node> ();
        removedNode.add(node);
        removeNodes(removedNode);
    }
    /**
     * Removes a collection of nodes from the selection.
     *
     * @param nodes                         the collection of nodes to remove from the selection
     */
    public void removeNodes(Collection nodes) {
        if (m_selectedNodes.removeAll(nodes))
            fireNodesRemovedFromSelection(nodes);
    }
    /**
     * Clears the selection.
     */
    public void clear() {
        if (!m_selectedNodes.isEmpty()) {
            m_selectedNodes.clear();
            fireSelectionCleared();
        }
    }
    /**
     * Returns the selection.
     *
     * @return                              the selection
     */
    public Collection <Node> getSelectedNodes() {
        return Collections.unmodifiableCollection(m_selectedNodes);
    }
    /**
     * Checks whteher the node has been selected.
     *
     * @param node                          the node that is checked
     * @return                              <code>true</code> if the node has been selected
     */
    public boolean isNodeSelected(Node node) {
        return m_selectedNodes.contains(node);
    }
    /**
     * Adds a listener to the selection.
     *
     * @param listener                      the listener to add to the selection
     */
    public void addNodeSelectionListener(NodeSelectionListener listener) {
        m_eventListenerList.add(NodeSelectionListener.class,listener);
    }
    /**
     * Removes a listener from the selection.
     *
     * @param listener                      the listener to add to the selection
     */
    public void removeNodeSelectionListener(NodeSelectionListener listener) {
        m_eventListenerList.remove(NodeSelectionListener.class,listener);
    }
    /**
     * Fires the event that the nodes have been added to the selection.
     *
     * @param nodes                         the collection of nodes added to the selection
     */
    protected void fireNodesAddedToSelection(Collection nodes) {
        EventListener[] listeners=m_eventListenerList.getListeners(NodeSelectionListener.class);
        for (int i=0;i<listeners.length;i++)
            ((NodeSelectionListener)listeners[i]).nodesAddedToSelection(this,nodes);
    }
    /**
     * Fires the event that the nodes have been removed from the selection.
     *
     * @param nodes                         the collection of nodes removed from the selection
     */
    protected void fireNodesRemovedFromSelection(Collection nodes) {
        EventListener[] listeners=m_eventListenerList.getListeners(NodeSelectionListener.class);
        for (int i=0;i<listeners.length;i++)
            ((NodeSelectionListener)listeners[i]).nodesRemovedFromSelection(this,nodes);
    }
    /**
     * Fires the event that the selection has been cleared.
     */
    protected void fireSelectionCleared() {
        EventListener[] listeners=m_eventListenerList.getListeners(NodeSelectionListener.class);
        for (int i=0;i<listeners.length;i++)
            ((NodeSelectionListener)listeners[i]).selectionCleared(this);
    }

    /**
     * Handles graph events.
     */
    protected class GraphHandler implements GraphListener {
        public void graphLayoutUpdated(Graph graph) {
        }
        public void graphUpdated(Graph graph) {
        }
        public void graphContentsChanged(Graph graph) {
            clear();
        }
        public void elementsAdded(Graph graph,Collection nodes,Collection edges) {
        }
        public void elementsRemoved(Graph graph,Collection nodes,Collection edges) {
            if (nodes!=null) {
                Set <Node> toRemove=null;
                Iterator iterator=nodes.iterator();
                while (iterator.hasNext()) {
                    Node node=(Node)iterator.next();
                    if (m_selectedNodes.contains(node)) {
                        if (toRemove==null)
                            toRemove=new HashSet <Node> ();
                        toRemove.add(node);
                    }
                }
                if (toRemove!=null)
                    removeNodes(toRemove);
            }
        }
    }
}
