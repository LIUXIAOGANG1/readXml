package net.sourceforge.jpowergraph.layout.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.layout.LayoutStrategy;

public class SpringLayoutStrategy implements LayoutStrategy {
    
    protected Graph graph;
    protected List <SubGraph> subGraphs = new LinkedList <SubGraph> ();
    protected Map <Node, NodeMovement> m_nodeMovement = new HashMap <Node, NodeMovement> ();
    
    protected double damper=0.0;
    protected double maxMotion=0;
    protected double motionRatio=0;
    protected boolean damping=true;
    protected double rigidity = 1;

    public SpringLayoutStrategy(Graph theGraph) {
        graph = theGraph;
    }

    public Graph getGraph() {
        return graph;
    }

    public NodeMovement getNodeMovement(Node node) {
        return m_nodeMovement.get(node);
    }
    
    public void elementsAdded(Collection <Node> nodes, Collection <Edge> edges) {
        if (nodes != null) {
            for (Node node : nodes){
                addNode(node);
            }
        }
        if (edges != null) {
            for (Edge edge : edges){
                addEdge(edge);
            }
        }
    }

    public void elementsRemoved(Collection <Node> nodes, Collection <Edge> edges) {
        if (nodes != null) {
            for (Node node : nodes){
                m_nodeMovement.remove(node);
            }
        }
        if (edges != null) {
            for (Edge edge : edges){
                nodeChanged(edge.getFrom());
                nodeChanged(edge.getTo());
            }
        }
        
        subGraphs.clear();
        
        for (NodeMovement nodeMovement : m_nodeMovement.values()){
            subGraphs.add(new SubGraph(nodeMovement));
        }
        
        for (Edge edge : graph.getVisibleEdges()){
            addNode(edge.getFrom());
            addNode(edge.getTo());
            addEdge(edge);
        }
    }
    
    private void nodeChanged(Node node) {
        NodeMovement nodeMovement = getNodeMovement(node);
        if (nodeMovement != null) {
            nodeMovement.setJustChanged(true);
            nodeMovement.setTimeWhenNodeBecomesNormal(System.currentTimeMillis() + 200);
        }
    }

    private void addNode(Node node) {
        if (!m_nodeMovement.containsKey(node)) {
            NodeMovement nodeMovement = new NodeMovement(node);
            m_nodeMovement.put(node, nodeMovement);
            subGraphs.add(new SubGraph(nodeMovement));
        }
        nodeChanged(node);
    }

    private void addEdge(Edge edge) {
        NodeMovement nodeMovement1 = getNodeMovement(edge.getFrom());
        NodeMovement nodeMovement2 = getNodeMovement(edge.getTo());
        if (nodeMovement1.getSubGraph() != nodeMovement2.getSubGraph()) {
            mergeSubGraphs(nodeMovement1.getSubGraph(), nodeMovement2.getSubGraph());
        }
    }
    
    public void graphContentsChanged() {
        m_nodeMovement.clear();
        subGraphs.clear();
        for (Node node : graph.getVisibleNodes()){
            addNode(node);
        }
        for (Edge edge : graph.getVisibleEdges()){
            addEdge(edge);
        }
    }

    public void notifyGraphLayoutUpdated() {
        damping=true;
        damper=1.0;
    }
    
    public void executeGraphLayoutStep() {
        for (int i=0;i<10;i++) {
            relaxEdges();
            adjustNodePairs();
            moveNodes();
            damp();
        }
    }

    public boolean shouldExecuteStep() {
        return !(damper < 0.1 && damping && maxMotion < 0.001);
    }

    protected void relaxEdges() {
        for (Edge edge : getGraph().getVisibleEdges()){
            double deltaX = edge.getTo().getX() - edge.getFrom().getX();
            double deltaY = edge.getTo().getY() - edge.getFrom().getY();
            double currentLength = Math.sqrt(deltaX*deltaX+deltaY*deltaY);
            double factor = rigidity * currentLength / (edge.getLength()*100.0);
            
            applyDelta(getNodeMovement(edge.getFrom()), deltaX * factor, deltaY * factor);
            applyDelta(getNodeMovement(edge.getTo()), -(deltaX * factor), -(deltaY * factor));
        }
    }

    private void adjustNodePairs() {
        for (SubGraph subGraph1 : subGraphs){
            for (NodeMovement node1Movement : subGraph1.getNodeMovements()){
                for (SubGraph subGraph2 : subGraphs){
                    for (NodeMovement node2Movement : subGraph2.getNodeMovements()){
                        Node node1 = node1Movement.getNode();
                        Node node2 = node2Movement.getNode();
                        if (node1 != node2) {
                            double factor = 100.0 * node1.getRepulsion() * node2.getRepulsion() * rigidity;
                            
                            double dx = 0;
                            double dy = 0;
                            double deltaX = node1.getX() - node2.getX();
                            double deltaY = node1.getY() - node2.getY();
                            double currentLengthSquared = deltaX * deltaX + deltaY * deltaY;
                            boolean sameSubGraph = node1Movement.getSubGraph().contains(node2Movement);
                            
                            if (Math.abs(currentLengthSquared) < 0.1) {
                                dx = Math.random();
                                dy = Math.random();
                                
                                if (!sameSubGraph && dx < 0.5){
                                    dx *= 2;
                                }
                                if (!sameSubGraph && dy < 0.5){
                                    dy *= 2;
                                }
                            }
                            else if (!sameSubGraph && currentLengthSquared < 200 * 200) {
                                dx = deltaX/currentLengthSquared;         
                                dy = deltaY/currentLengthSquared;         
                            }
                            else if (sameSubGraph && currentLengthSquared < 600 * 600) {
                                dx = deltaX/currentLengthSquared;         
                                dy = deltaY/currentLengthSquared;         
                            }
                            
                            applyDelta(node1Movement, dx * factor, dy * factor);
                            applyDelta(node2Movement, -(dx * factor), -(dy * factor));
                        }
                    }
                }
            }
        }
    }

    protected void moveNodes() {
        double lastMaxMotion = maxMotion;
        maxMotion=0;
        
        for (Node node : getGraph().getVisibleNodes()){
            NodeMovement movement = getNodeMovement(node);
            double dx = movement.getXDelta() * damper;
            double dy = movement.getYDelta() * damper;
            movement.setXDelta(dx/2);
            movement.setYDelta(dy/2);
            if (!node.isFixed()) {
                double x = node.getX() + Math.max(-30, Math.min(30, dx));
                double y = node.getY() + Math.max(-30, Math.min(30, dy));
                node.setLocation(x, y);
                maxMotion = Math.max(Math.sqrt(dx*dx+dy*dy), maxMotion);
            }
            if (movement.isJustChanged() && System.currentTimeMillis() > movement.getTimeWhenNodeBecomesNormal()){
                movement.setJustChanged(false);
            }
        }
        if (maxMotion > 0){
            motionRatio = lastMaxMotion/maxMotion-1; //subtract 1 to make a positive value mean that things are moving faster
        }
        else{
            motionRatio = 0;
        }
    }

    protected void applyDelta(NodeMovement nodeMovement, double dx, double dy) {
        if (nodeMovement != null){
            int div = 1;
            if (nodeMovement.isJustChanged()){
                div = 10;
            }
            nodeMovement.setXDelta((nodeMovement.getXDelta() + dx)/div);
            nodeMovement.setYDelta((nodeMovement.getYDelta() + dy)/div);
        }
    }

    public void stopDamper() {
        damping=false;
        damper=1.0;
    }

    public void stopMotion() {
        damping=true;
        if (damper>0.3){
            damper=0.3;
        }
        else{
            damper=0;
        }
    }

    public void damp() {
        if (damping) {
            if (motionRatio <= 0.001) {
                if ((maxMotion < 0.2 || (maxMotion > 1 && damper < 0.9)) && damper > 0.01){
                    damper-=0.01;
                }
                else if (maxMotion < 0.4 && damper > 0.003){
                    damper-=0.003;
                }
                else if(damper > 0.0001){
                    damper -=0.0001;
                }
            }
        }
        if(maxMotion < 0.001 && damping){
            damper=0;
        }
    }
    
    public void mergeSubGraphs(SubGraph subGraph1, SubGraph subGraph2) {
        if (subGraph1.getNodeMovements().size() < subGraph2.getNodeMovements().size()) {
            subGraph2.mergeWith(subGraph1);
            subGraphs.remove(subGraph1);
        }
        else {
            subGraph1.mergeWith(subGraph2);
            subGraphs.remove(subGraph2);
        }
    }
}
