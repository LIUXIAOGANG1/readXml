package net.sourceforge.jpowergraph;


import java.util.Collection;
import java.util.EventListener;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;

/**
 * The listener for graph events.
 */
public interface GraphListener extends EventListener {
    /**
     * Called when graph layout is updated.
     *
     * @param graph                     the graph
     */
    void graphLayoutUpdated(Graph graph);
    /**
     * Called when graph has changed, but no nodes have changed their position.
     *
     * @param graph                     the graph
     */
    void graphUpdated(Graph graph);
    /**
     * Called when graph contents is radically changed.
     *
     * @param graph                     the graph
     */
    void graphContentsChanged(Graph graph);
    /**
     * Called when elements are added to the graph.
     *
     * @param graph                     the graph
     * @param nodes                     the nodes added (may be <code>null</code>)
     * @param edges                     the edges added (may be <code>null</code>)
     */
    void elementsAdded(Graph graph, Collection <Node> nodes, Collection <Edge> edges);
    /**
     * Called when elements are removed from the graph.
     *
     * @param graph                     the graph
     * @param nodes                     the nodes added (may be <code>null</code>)
     * @param edges                     the edges added (may be <code>null</code>)
     */
    void elementsRemoved(Graph graph, Collection <Node> nodes, Collection <Edge> edges);
}
