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

package net.sourceforge.jpowergraph.manipulator.selection;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.manipulator.AbstractManipulator;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.listeners.JPowerGraphMouseEvent;

public class DoubleClickManipulator extends AbstractManipulator {
    public static final String NAME = "DoubleClickManipulator";

    private DoubleClickListener doubleClickListener;

    public DoubleClickManipulator(JGraphPane theGraphPane, DoubleClickListener theDoubleClickListener) {
        setGraphPane(theGraphPane);
        this.doubleClickListener = theDoubleClickListener;
    }

    public String getName() {
        return NAME;
    }

    public void mouseDoubleClick(JPowerGraphMouseEvent e) {
        if (getGraphPane().isEnabled() && e.getButton() == JPowerGraphMouseEvent.LEFT && doubleClickListener != null) {
            Legend legend = getGraphPane().getLegendAtPoint(e.getPoint());
            Node node = getGraphPane().getNodeAtPoint(e.getPoint());
            Edge edge = getGraphPane().getNearestEdge(e.getPoint());

            if (legend != null) {
                doubleClickListener.doubleClick(e, legend);
            }
            else if (node != null) {
                doubleClickListener.doubleClick(e, node);
            }
            else if (edge != null) {
                doubleClickListener.doubleClick(e, edge);
            }
            else {
                doubleClickListener.doubleClick(e);
            }
        }
    }

}
