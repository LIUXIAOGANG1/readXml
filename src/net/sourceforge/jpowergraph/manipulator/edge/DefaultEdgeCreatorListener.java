package net.sourceforge.jpowergraph.manipulator.edge;

import java.util.ArrayList;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.defaults.DefaultEdge;

/**
 * @author Mick Kerrigan
 *
 * Created on 05-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/common/src/net/sourceforge/jpowergraph/manipulator/edge/DefaultEdgeCreatorListener.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:35:40 $
 */

public class DefaultEdgeCreatorListener implements EdgeCreatorListener{

    private Graph graph;

    public DefaultEdgeCreatorListener(Graph theGraph) {
        this.graph = theGraph;
    }

    public boolean canCreateEdgeFrom(Node theFrom) {
        return theFrom != null;
    }

    public boolean canCreateEdge(Node theFrom, Node theTo) {
        return theFrom != null && theTo != null;
    }

    public void createEdge(Node theFrom, Node theTo) {
        ArrayList <Edge> newEdges = new ArrayList <Edge> ();
        newEdges.add(new DefaultEdge(theFrom, theTo));
        graph.addElements(new ArrayList <Node> (), newEdges);
    }
}