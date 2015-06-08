/*
 * Copyright (C) 2006 Digital Enterprise Research Insitute (DERI) Innsbruck
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.sourceforge.jpowergraph.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.jpowergraph.ClusterNode;
import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.GroupLegendItem;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.NodeFilter;
import net.sourceforge.jpowergraph.defaults.ClusterEdge;
import net.sourceforge.jpowergraph.defaults.DefaultGraph;
import net.sourceforge.jpowergraph.defaults.DefaultNodeFilter;
import net.sourceforge.jpowergraph.defaults.LoopEdge;
import net.sourceforge.jpowergraph.defaults.TextEdge;
import net.sourceforge.jpowergraph.example.edges.ArrowEdge;
import net.sourceforge.jpowergraph.example.edges.LineEdge;
import net.sourceforge.jpowergraph.example.edges.LoopEdge2;
import net.sourceforge.jpowergraph.example.nodes.NodeType1;
import net.sourceforge.jpowergraph.example.nodes.NodeType2;
import net.sourceforge.jpowergraph.example.nodes.NodeType3;
import net.sourceforge.jpowergraph.example.nodes.NodeType4;
import net.sourceforge.jpowergraph.lens.CursorLens;
import net.sourceforge.jpowergraph.lens.LegendLens;
import net.sourceforge.jpowergraph.lens.LensSet;
import net.sourceforge.jpowergraph.lens.NodeSizeLens;
import net.sourceforge.jpowergraph.lens.RotateLens;
import net.sourceforge.jpowergraph.lens.TooltipLens;
import net.sourceforge.jpowergraph.lens.TranslateLens;
import net.sourceforge.jpowergraph.lens.ZoomLens;
import net.sourceforge.jpowergraph.manipulator.dragging.DraggingManipulator;
import net.sourceforge.jpowergraph.manipulator.edge.DefaultEdgeCreatorListener;
import net.sourceforge.jpowergraph.manipulator.edge.EdgeCreatorManipulator;
import net.sourceforge.jpowergraph.manipulator.popup.PopupManipulator;
import net.sourceforge.jpowergraph.manipulator.selection.DefaultNodeSelectionModel;
import net.sourceforge.jpowergraph.manipulator.selection.NodeSelectionListener;
import net.sourceforge.jpowergraph.manipulator.selection.NodeSelectionModel;
import net.sourceforge.jpowergraph.manipulator.selection.SelectionManipulator;
import net.sourceforge.jpowergraph.painters.edge.ArrowEdgePainter;
import net.sourceforge.jpowergraph.painters.edge.ClusterEdgePainter;
import net.sourceforge.jpowergraph.painters.edge.LineEdgePainter;
import net.sourceforge.jpowergraph.painters.edge.LineWithTextEdgePainter;
import net.sourceforge.jpowergraph.painters.edge.LoopEdgePainter;
import net.sourceforge.jpowergraph.painters.node.ClusterNodePainter;
import net.sourceforge.jpowergraph.painters.node.RowImageMap;
import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;


public class Example {

    private LensSet lensSet;
    
    public Example(){
        TranslateLens m_translateLens=new TranslateLens();
        ZoomLens m_zoomLens= new ZoomLens();
        RotateLens m_rotateLens= new RotateLens();
        CursorLens m_cursorLens = new CursorLens();
        TooltipLens m_tooltipLens = new TooltipLens();
        LegendLens m_legendLens = new LegendLens();
        NodeSizeLens m_nodeSizelens = new NodeSizeLens();
//        m_nodeSizelens.setNodeSize(NodePainter.SMALL);
        lensSet = new LensSet();
        lensSet.addLens(m_zoomLens);
        lensSet.addLens(m_rotateLens);
        lensSet.addLens(m_cursorLens);
        lensSet.addLens(m_tooltipLens);
        lensSet.addLens(m_legendLens);
        lensSet.addLens(m_nodeSizelens);
        lensSet.addLens(m_translateLens);
    }
    
    public DefaultGraph createGraph(){
        DefaultGraph graph = new DefaultGraph();
        ArrayList <Node> nodes = new ArrayList <Node> ();
        ArrayList <Edge> edges = new ArrayList <Edge> ();
        
        Node n1 = new NodeType1("Node 1");
        Node sn1 = new NodeType2("Node 2");
        Node sn2 = new NodeType2("Node 3");
        ClusterNode sn3 = new NodeType4("Node 4", 19);
        Node sn4 = new NodeType2("Node 5");
        Node sn5 = new NodeType2("Node 6");
        Node sn6 = new NodeType2("Multi Line\nNode Label");
        Node ssn1 = new NodeType3("Node 7");
        Node ssn2 = new NodeType3("Node 8");
        
        nodes.add(n1);
        nodes.add(sn1);
        nodes.add(sn2);
        nodes.add(sn3);
        nodes.add(sn4);
        nodes.add(sn5);
        nodes.add(sn6);
        nodes.add(ssn1);
        nodes.add(ssn2);
        
        edges.add(new LineEdge(n1, sn1));
        edges.add(new TextEdge(n1, sn2, "edge label"));
        edges.add(new ClusterEdge(n1, sn3, true));
        edges.add(new LoopEdge2(sn3));
        edges.add(new LineEdge(n1, sn4));
        edges.add(new LoopEdge(sn4));
        edges.add(new LineEdge(n1, sn5));
        edges.add(new LoopEdge2(sn5));
        edges.add(new LineEdge(n1, sn6));
        edges.add(new ArrowEdge(sn1, ssn1));
        edges.add(new ArrowEdge(sn1, ssn2));
        
        graph.addElements(nodes, edges);
        return graph;
    }
    
    public DefaultGraph getSubGraph(Collection <Node> selectedNodes){
        DefaultGraph graph = new DefaultGraph();
        for (Node n : selectedNodes){
            List <Node> nodes = new ArrayList <Node> ();
            List <Edge> edges = new ArrayList <Edge> ();
            nodes.add(n);
            
            Pair<List <Node>, List<Edge>> p = getParents(n);
            nodes.addAll(p.getFirst());
            edges.addAll(p.getSecond());
            
            Pair<List <Node>, List<Edge>> c = getDirectChildren(n);
            nodes.addAll(c.getFirst());
            edges.addAll(c.getSecond());
            
            graph.addElements(nodes, edges);
        }
        return graph;
    }
    
    public Pair<List <Node>, List<Edge>> getParents(Node theNode){
        List <Node> nodes = new ArrayList <Node> ();
        List <Edge> edges = new ArrayList <Edge> ();
        for (Edge edge : theNode.getEdgesTo()){
            edges.add(edge);
            nodes.add(edge.getFrom());
        }
        
        return new Pair<List <Node>, List<Edge>>(nodes, edges);
    }
    
    public Pair<List <Node>, List<Edge>> getDirectChildren(Node theNode){
        List <Node> nodes = new ArrayList <Node> ();
        List <Edge> edges = new ArrayList <Edge> ();
        for (Edge edge : theNode.getEdgesFrom()){
            edges.add(edge);
            nodes.add(edge.getTo());
        }
        return new Pair<List <Node>, List<Edge>>(nodes, edges);
    }
    
    public void setPainters(JGraphPane theJGraphPane){
        JPowerGraphColor light_blue = new JPowerGraphColor(102, 204, 255);
        JPowerGraphColor dark_blue = new JPowerGraphColor(0, 153, 255);
        JPowerGraphColor light_red = new JPowerGraphColor(255, 102, 102);
        JPowerGraphColor dark_red = new JPowerGraphColor(204, 51, 51);
        JPowerGraphColor light_green = new JPowerGraphColor(153, 255, 102);
        JPowerGraphColor dark_green = new JPowerGraphColor(0, 204, 0);
        JPowerGraphColor black = new JPowerGraphColor(0, 0, 0);
        JPowerGraphColor gray = new JPowerGraphColor(128, 128, 128);
        
        theJGraphPane.setNodePainter(NodeType1.class, new ShapeNodePainter(ShapeNodePainter.RECTANGLE, light_blue, dark_blue, black));
        theJGraphPane.setNodePainter(NodeType2.class, new ShapeNodePainter(ShapeNodePainter.ELLIPSE, light_red, dark_red, black));
        theJGraphPane.setNodePainter(NodeType3.class, new ShapeNodePainter(ShapeNodePainter.TRIANGLE, light_green, dark_green, black));
        theJGraphPane.setNodePainter(NodeType4.class, new ClusterNodePainter(ShapeNodePainter.ELLIPSE, light_red, dark_red, black, true, new RowImageMap()));
        theJGraphPane.setEdgePainter(LoopEdge.class, new LoopEdgePainter(gray, gray, LoopEdgePainter.CIRCULAR));
        theJGraphPane.setEdgePainter(LoopEdge2.class, new LoopEdgePainter(gray, gray, LoopEdgePainter.RECTANGULAR));
        theJGraphPane.setEdgePainter(TextEdge.class, new LineWithTextEdgePainter(gray, gray, false));
        theJGraphPane.setEdgePainter(LineEdge.class, new LineEdgePainter(gray, gray, true));
        theJGraphPane.setEdgePainter(ArrowEdge.class, new ArrowEdgePainter <Edge>());
        theJGraphPane.setEdgePainter(ClusterEdge.class, new ClusterEdgePainter <ClusterEdge>());
        theJGraphPane.setDefaultEdgePainter(new LineEdgePainter(gray, gray, false));
    }

    public NodeFilter getNodeFilter() {
        NodeFilter nodeFilter = new DefaultNodeFilter();
        nodeFilter.addFilterable(NodeType2.class, true);
        nodeFilter.addFilterable(NodeType3.class, true);
        nodeFilter.addDependancy(NodeType2.class, NodeType3.class);
        return nodeFilter;
    }

    public LensSet getLensSet() {
        return lensSet;
    }

    public void addManipulatorsAndSelectionModel(final JGraphPane theJGraphPane) {
        NodeSelectionModel nodeSelectionModel = new DefaultNodeSelectionModel(theJGraphPane.getGraph());
        nodeSelectionModel.addNodeSelectionListener(new NodeSelectionListener() {
            public void selectionCleared(NodeSelectionModel nodeSelectionModel) {
                theJGraphPane.getSubGraphHighlighter().setSubGraph(null);
            }
        
            public void nodesRemovedFromSelection(NodeSelectionModel nodeSelectionModel, Collection nodes) {
                if (nodeSelectionModel.getSelectedNodes().size() == 0){
                    theJGraphPane.getSubGraphHighlighter().setSubGraph(null);
                }
                else{
                    theJGraphPane.getSubGraphHighlighter().setSubGraph(getSubGraph(nodeSelectionModel.getSelectedNodes()));
                }
            }
        
            public void nodesAddedToSelection(NodeSelectionModel nodeSelectionModel, Collection nodes) {
                theJGraphPane.getSubGraphHighlighter().setSubGraph(getSubGraph(nodeSelectionModel.getSelectedNodes()));
            }
        });
        
        theJGraphPane.addManipulator(new SelectionManipulator(nodeSelectionModel));
        theJGraphPane.addManipulator(new DraggingManipulator((CursorLens) getLensSet().getFirstLensOfType(CursorLens.class)));
        theJGraphPane.addManipulator(new EdgeCreatorManipulator((CursorLens) getLensSet().getFirstLensOfType(CursorLens.class), new DefaultEdgeCreatorListener(theJGraphPane.getGraph())));
        theJGraphPane.addManipulator(new PopupManipulator(theJGraphPane, (TooltipLens) getLensSet().getFirstLensOfType(TooltipLens.class)));
    }
    
    class Pair <E, F>{
        
        private E e;
        private F f;

        public Pair (E theE, F theF){
            this.e = theE;
            this.f = theF;
        }

        public E getFirst() {
            return e;
        }

        public F getSecond() {
            return f;
        }
    }

    public GroupLegendItem getLegendRoot() {
        GroupLegendItem root = new GroupLegendItem();
        
        GroupLegendItem group1 = new GroupLegendItem("Group 1");
        group1.addGroupClass(NodeType1.class);
        group1.addGroupClass(NodeType3.class);
        root.add(group1);
        
        GroupLegendItem group2 = new GroupLegendItem("Group 2");
        group2.addGroupClass(NodeType2.class);
//        group2.addGroupClass(GroupLegendItem.class);
        root.add(group2);
        
        GroupLegendItem group3 = new GroupLegendItem("Group 3");
        root.add(group3);
        
        GroupLegendItem group4 = new GroupLegendItem("Group 4");
        root.add(group4);
        
        GroupLegendItem group5 = new GroupLegendItem("Group 5");
        root.add(group5);
        
        return root;
    }
}