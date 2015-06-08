package net.sourceforge.jpowergraph.manipulator.selection;

import java.util.Collection;
import java.util.EventListener;

/**
 * A listener for node selection.
 */
public interface NodeSelectionListener extends EventListener {
    /**
     * Called when nodes are added to the selection.
     *
     * @param nodeSelectionModel                    the selection of the nodes
     * @param nodes                                 the nodes added to the selection
     */
    void nodesAddedToSelection(NodeSelectionModel nodeSelectionModel,Collection nodes);
    /**
     * Called when nodes are removed to the selection.
     *
     * @param nodeSelectionModel                    the selection of the nodes
     * @param nodes                                 the nodes removed to the selection
     */
    void nodesRemovedFromSelection(NodeSelectionModel nodeSelectionModel,Collection nodes);
    /**
     * Called when the selection is cleared.
     *
     * @param nodeSelectionModel                    the selection of the nodes
     */
    void selectionCleared(NodeSelectionModel nodeSelectionModel);
}
