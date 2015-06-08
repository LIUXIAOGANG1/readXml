package net.sourceforge.jpowergraph.manipulator.edge;

import net.sourceforge.jpowergraph.Node;

/**
 * @author Mick Kerrigan
 *
 * Created on 05-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/common/src/net/sourceforge/jpowergraph/manipulator/edge/EdgeCreatorListener.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:35:40 $
 */
public interface EdgeCreatorListener {

    public boolean canCreateEdgeFrom(Node theFrom);
    public boolean canCreateEdge(Node theFrom, Node theTo);
    public void createEdge(Node theFrom, Node theTo);
}
