package net.sourceforge.jpowergraph.swing.manipulator;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.lens.LegendLens;
import net.sourceforge.jpowergraph.lens.LensSet;
import net.sourceforge.jpowergraph.lens.RotateLens;
import net.sourceforge.jpowergraph.lens.TooltipLens;
import net.sourceforge.jpowergraph.lens.ZoomLens;
import net.sourceforge.jpowergraph.manipulator.popup.ContextMenuListener;

/**
 * @author Mick Kerrigan
 *
 * Created on 03-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/swing/src/net/sourceforge/jpowergraph/swing/manipulator/DefaultSwingContextMenuListener.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:37:12 $
 */

public class DefaultSwingContextMenuListener implements ContextMenuListener <JPopupMenu>{

    private Graph graph;
    private LegendLens legendLens;
    private ZoomLens zoomLens;
    private Integer[] zoomLevels;
    private RotateLens rotateLens;
    private Integer[] rotateAngles;
    private TooltipLens tooltipLens;
    
    public DefaultSwingContextMenuListener(Graph theGraph, LensSet theLensSet, Integer[] theZoomLevels, Integer[] theRotateAngles){
        this.graph = theGraph;
        this.legendLens = (LegendLens) theLensSet.getFirstLensOfType(LegendLens.class);
        this.zoomLens = (ZoomLens) theLensSet.getFirstLensOfType(ZoomLens.class);
        this.zoomLevels = theZoomLevels;
        this.rotateLens = (RotateLens) theLensSet.getFirstLensOfType(RotateLens.class);
        this.rotateAngles = theRotateAngles;
        this.tooltipLens = (TooltipLens) theLensSet.getFirstLensOfType(TooltipLens.class);
    }
    
    public void fillNodeContextMenu(final Node theNode, JPopupMenu theMenu) {
        JMenuItem deleteNode = new JMenuItem(new AbstractAction("Delete " + theNode.getLabel()) {
            public void actionPerformed(ActionEvent e) {
                ArrayList <Node> nodes = new ArrayList <Node> ();
                nodes.add(theNode);
                ArrayList <Edge> edges = new ArrayList <Edge> ();
                for (Iterator i = graph.getVisibleEdges().iterator(); i.hasNext();) {
                    Edge edge = (Edge) i.next();
                    if (edge.getFrom().equals(theNode) || edge.getTo().equals(theNode)){
                        edges.add(edge);
                    }
                }
                graph.deleteElements(nodes, edges);
            }
        });
        theMenu.add(deleteNode);
    }

    public void fillEdgeContextMenu(final Edge theEdge, JPopupMenu theMenu) {
        JMenuItem deleteEdge = new JMenuItem(new AbstractAction("Delete Edge") {
            public void actionPerformed(ActionEvent e) {
                ArrayList <Edge> edges = new ArrayList <Edge> ();
                edges.add(theEdge);
                graph.deleteElements(new ArrayList <Node> (), edges);
            }
        });
        theMenu.add(deleteEdge);
    }

    public void fillLegendContextMenu(Legend theLegend, JPopupMenu theMenu) {
        if (legendLens != null){
            JMenuItem hideLegend = new JMenuItem(new AbstractAction("Hide Legend") {
                public void actionPerformed(ActionEvent e) {
                    legendLens.setShowLegend(false);
                }
            });
            theMenu.add(hideLegend);
        }
    }
    
    public void fillBackgroundContextMenu(JPopupMenu theMenu) {
        if (zoomLens != null){
            final Integer[] zoom = zoomLevels;
            final int index = Arrays.binarySearch(zoom, (int) (zoomLens.getZoomFactor() * 100d));
            
            JMenuItem zoomIn = new JMenuItem(new AbstractAction("Zoom In") {
                public void actionPerformed(ActionEvent e) {
                    zoomLens.setZoomFactor(zoom[index + 1]/ 100d);
                }
            });
            zoomIn.setEnabled(index != zoom.length - 1);
            theMenu.add(zoomIn);
            JMenuItem zoomOut = new JMenuItem(new AbstractAction("Zoom Out") {
                public void actionPerformed(ActionEvent e) {
                    zoomLens.setZoomFactor(zoom[index - 1]/ 100d);
                }
            });
            zoomOut.setEnabled(index != 0);
            theMenu.add(zoomOut);
        }
        theMenu.addSeparator();
        if (rotateLens != null){
            final Integer[] rotate = rotateAngles;
            int currentValue = (int) (360 - rotateLens.getRotationAngle());
            while (currentValue == 360){
                currentValue = 0;
            }
            final int index = Arrays.binarySearch(rotate, currentValue);
            
            JMenuItem rotateClockwise = new JMenuItem(new AbstractAction("Rotate ClockWise") {
                public void actionPerformed(ActionEvent e) {
                    if (index == rotate.length - 1){
                        rotateLens.setRotationAngle(360 - rotate[0]);
                    }
                    else{
                        rotateLens.setRotationAngle(360 - rotate[index + 1]);
                    }
                }
            });
            theMenu.add(rotateClockwise);
            
            JMenuItem rotateCounterClockwise = new JMenuItem(new AbstractAction("Rotate Counter ClockWise") {
                public void actionPerformed(ActionEvent e) {
                    if (index == 0){
                        rotateLens.setRotationAngle(360 - rotate[rotate.length - 1]);
                    }
                    else{
                        rotateLens.setRotationAngle(360 - rotate[index - 1]);
                    }
                }
            });
            theMenu.add(rotateCounterClockwise);
        }
        theMenu.addSeparator();
        if (legendLens != null){
            if (legendLens.isShowLegend()){
                JMenuItem hideLegend = new JMenuItem(new AbstractAction("Hide Legend") {
                    public void actionPerformed(ActionEvent e) {
                        legendLens.setShowLegend(false);
                    }
                });
                theMenu.add(hideLegend);
            }
            else{
                JMenuItem showLegend = new JMenuItem(new AbstractAction("Show Legend") {
                    public void actionPerformed(ActionEvent e) {
                        legendLens.setShowLegend(true);
                    }
                });
                theMenu.add(showLegend);
            }
        }
        if (tooltipLens != null){
            if (tooltipLens.isShowToolTips()){
                JMenuItem hideToolTips = new JMenuItem(new AbstractAction("Hide Tooltips") {
                    public void actionPerformed(ActionEvent e) {
                        tooltipLens.setShowToolTips(false);
                    }
                });
                theMenu.add(hideToolTips);
            }
            else{
                JMenuItem showToolTips = new JMenuItem(new AbstractAction("Show Tooltips") {
                    public void actionPerformed(ActionEvent e) {
                        tooltipLens.setShowToolTips(true);
                    }
                });
                theMenu.add(showToolTips);
            }
        }
    }
}
