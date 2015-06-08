package net.sourceforge.jpowergraph.layout;

import java.util.Collection;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.Node;

/**
 * A layout strategy used by the layouter. The layouter provides a thread container for the
 * strategy, which then doesn't have to worry about synchronization and threading.
 */
public interface LayoutStrategy {
    /**
     * Returns the graph of the strategy.
     *
     * @return                              the graph of the strategy
     */
    Graph getGraph();
    /**
     * Notifies the strategy that elements were inserted into the graph.
     *
     * @param nodes                         the inserted nodes (may be <code>null</code>)
     * @param edges                         the inserted edges (may be <code>null</code>)
     */
    void elementsAdded(Collection <Node> nodes,Collection <Edge> edges);
    /**
     * Notifies the strategy that elements were removed from the graph.
     *
     * @param nodes                         the removed nodes (may be <code>null</code>)
     * @param edges                         the removed edges (may be <code>null</code>)
     */
    void elementsRemoved(Collection <Node> nodes,Collection <Edge> edges);
    /**
     * Notifies the strategy that the graph has been radically changed.
     */
    void graphContentsChanged();
    /**
     * Notifies the strategy that the graph has changed due tue an event that is not caused by
     * the strategy itself.
     */
    void notifyGraphLayoutUpdated();
    /**
     * Executes one step in the layout.
     */
    void executeGraphLayoutStep();
    /**
     * Returns <code>true</code> if more steps in the layout should be executed.
     *
     * @return                              <code>true</code> if more steps should be executed
     */
    boolean shouldExecuteStep();
}
