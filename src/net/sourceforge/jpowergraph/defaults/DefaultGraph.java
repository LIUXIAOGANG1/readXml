package net.sourceforge.jpowergraph.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jpowergraph.AbstractGraph;
import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.NodeFilter;
import net.sourceforge.jpowergraph.NodeFilterListener;

/**
 * A default implementation of a mutable graph.
 */
public class DefaultGraph extends AbstractGraph implements NodeFilterListener{
    /** Nodes of the graph. */
    protected List<Node> m_nodes;

    /** Edges of the graph. */
    protected List<Edge> m_edges;

    private NodeFilter nodeFilter;

    /**
     * Creates a new graph.
     */
    public DefaultGraph() {
        m_nodes = new ArrayList<Node>();
        m_edges = new ArrayList<Edge>();
    }

    public List<Node> getAllNodes() {
        return m_nodes;
    }

    public List<Edge> getAllEdges() {
        return m_edges;
    }
    
    public List<Node> getVisibleNodes() {
        if (nodeFilter == null){
            return getAllNodes();
        }
        List <Node> result = new ArrayList <Node> ();
        for (Node node : m_nodes){
            if (nodeFilter.isNodeVisible(node, this)){
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Returns a collection of the edges in the graph.
     * 
     * @return the collection of the edges in the graph
     */
    public List<Edge> getVisibleEdges() {
        if (nodeFilter == null){
            return getAllEdges();
        }
        List <Edge> result = new ArrayList <Edge> ();
        for (Edge edge : m_edges){
            if (nodeFilter.isEdgeVisible(edge, this)){
                result.add(edge);
            }
        }
        return result;
    }

    /**
     * Adds elements to the graph.
     * 
     * @param nodes
     *            the nodes added to the graph (may be <code>null</code>)
     * @param edges
     *            the edges added to the graph (may be <code>null</code>)
     */
    public synchronized void addElements(Collection <Node> nodes, Collection <Edge> edges) {
        if (nodes != null) {
            Iterator iterator = nodes.iterator();
            while (iterator.hasNext()) {
                DefaultNode node = (DefaultNode) iterator.next();
                if (!m_nodes.contains(node))
                    m_nodes.add(node);
            }
        }
        if (edges != null) {
            Iterator iterator = edges.iterator();
            while (iterator.hasNext()) {
                Edge edge = (Edge) iterator.next();
                if (!m_edges.contains(edge)) {
                    m_edges.add(edge);
                    ((DefaultNode) edge.getFrom()).notifyEdgeAdded(edge);
                    ((DefaultNode) edge.getTo()).notifyEdgeAdded(edge);
                }
            }
        }
        fireElementsAdded(nodes, edges);
    }

    /**
     * Deletes elements from the graph.
     * 
     * @param nodes
     *            the nodes to delete (may be <code>null</code>)
     * @param edges
     *            the edges to delete (may be <code>null</code>)
     */
    public synchronized void deleteElements(Collection <Node> nodes, Collection <Edge> edges) {
        if (nodes == null){
            nodes = new HashSet <Node> ();
        }
        if (edges == null){
            edges = new HashSet <Edge> ();
        }
        
        for (Node node : nodes){
                m_nodes.remove(node);
            }
        
        for (Edge edge : m_edges){
            if (nodes.contains(edge.getFrom()) || nodes.contains(edge.getTo())){
                edges.add(edge);
                }
            }
        
        for(Edge edge : edges){
                if (m_edges.remove(edge)) {
                    ((DefaultNode) edge.getFrom()).notifyEdgeRemoved(edge);
                    ((DefaultNode) edge.getTo()).notifyEdgeRemoved(edge);
                }
            }
        fireElementsRemoved(nodes, edges);
    }

    /**
     * Merges the elements in the specified Collections with those already in
     * the graph.
     * 
     * If a node/edge in the specified Collection is not in the graph its is
     * added, if it is then it is ignored, if a node/edge in the graph is not in
     * the specified list then it is removedfrom the graph. This is done to
     * allow a complete state change of the graph without deleting the
     * nodeMovement values, this means that only changes between two graphs are
     * made.
     * 
     * @param nodes
     *            the nodes added to the graph (may be <code>null</code>)
     * @param edges
     *            the edges added to the graph (may be <code>null</code>)
     */
    public void mergeElements(Collection <Node> nodes, Collection <Edge> edges) {
        List <Node> nodesToRemove = new ArrayList <Node> ();
        List <Edge> edgesToRemove = new ArrayList <Edge> ();
        
        if (nodes != null) {
            for (Iterator i = m_nodes.iterator(); i.hasNext();) {
                Node cnode = (Node) i.next();
                if (nodes.contains(cnode)) {
                    nodes.remove(cnode);
                }
                else {
                    nodesToRemove.add(cnode);
                }
            }
        }
        if (edges != null) {
            for (Iterator i = m_edges.iterator(); i.hasNext();) {
                Edge cedge = (Edge) i.next();
                if (edges.contains(cedge)) {
                    edges.remove(cedge);
                }
                else {
                    edgesToRemove.add(cedge);
                }
            }
        }

        if (nodesToRemove.size() > 0 || edgesToRemove.size() > 0){
            deleteElements(nodesToRemove, edgesToRemove);
        }
        if ((nodes != null && nodes.size() > 0) || (edges != null && edges.size() > 0)) {
            addElements(nodes, edges);
        }
    }
    
    public NodeFilter getNodeFilter(){
        return nodeFilter;
    }
    
    public void setNodeFilter(NodeFilter theNodeFilter){
        if (nodeFilter != null){
            this.nodeFilter.removeNodeFilterListener(this);
        }
        this.nodeFilter = theNodeFilter;
        if (nodeFilter != null){
            this.nodeFilter.addNodeFilterListener(this);
        }
        filterStateChanged();
    }
    
    public void filterStateChanged(){
        List <Node> visibleNodes = new ArrayList <Node> ();
        visibleNodes.addAll(getVisibleNodes());
        List <Edge> visibleEdges = new ArrayList <Edge> ();
        visibleEdges.addAll(getVisibleEdges());
        
        List <Node> invisibleNodes = new ArrayList <Node> ();
        invisibleNodes.addAll(getAllNodes());
        invisibleNodes.removeAll(getVisibleNodes());
        List <Edge> invisibleEdges = new ArrayList <Edge> ();
        invisibleEdges.addAll(getAllEdges());
        invisibleEdges.removeAll(getVisibleEdges());
        
        if (visibleNodes.size() > 0 || visibleEdges.size() > 0){
            fireElementsAdded(visibleNodes, visibleEdges);
            }
        if (invisibleNodes.size() > 0 || invisibleEdges.size() > 0){
            fireElementsRemoved(invisibleNodes, invisibleEdges);
        }
        }
    
    /**
     * Clears the graph.
     */
    public synchronized void clear() {
        m_nodes.clear();
        m_edges.clear();
        fireGraphContentsChanged();
    }
}