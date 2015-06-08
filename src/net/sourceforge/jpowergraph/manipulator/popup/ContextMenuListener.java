package net.sourceforge.jpowergraph.manipulator.popup;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.Node;

/**
 * @author Mick Kerrigan
 *
 * Created on 02-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/common/src/net/sourceforge/jpowergraph/manipulator/popup/ContextMenuListener.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:35:39 $
 */
public interface ContextMenuListener <E> {

    public void fillNodeContextMenu(Node theNode, E theMenuWidget);
    public void fillEdgeContextMenu(Edge theEdge, E theMenuWidget);
    public void fillLegendContextMenu(Legend theLegend, E theMenuWidget);
    public void fillBackgroundContextMenu(E theMenuWidget);
}
