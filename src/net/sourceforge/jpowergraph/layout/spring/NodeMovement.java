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

package net.sourceforge.jpowergraph.layout.spring;

import net.sourceforge.jpowergraph.Node;

public class NodeMovement {

    private Node node;
    private SubGraph subGraph;

    private double xdelta = 0.0;
    private double ydelta = 0.0;

    private boolean justChanged = false;
    private long timeWhenNodeBecomesNormal = 0;

    public NodeMovement(Node theNode) {
        this.node = theNode;
    }

    public Node getNode() {
        return node;
    }

    public SubGraph getSubGraph() {
        return subGraph;
    }

    public void setSubGraph(SubGraph theSubGraph) {
        this.subGraph = theSubGraph;
    }

    public boolean isJustChanged() {
        return justChanged;
    }

    public void setJustChanged(boolean isJustChanged) {
        this.justChanged = isJustChanged;
    }

    public long getTimeWhenNodeBecomesNormal() {
        return timeWhenNodeBecomesNormal;
    }

    public void setTimeWhenNodeBecomesNormal(long theTimeWhenNodeBecomesNormal) {
        this.timeWhenNodeBecomesNormal = theTimeWhenNodeBecomesNormal;
    }

    public double getXDelta() {
        return xdelta;
    }

    public void setXDelta(double theXDelta) {
        this.xdelta = theXDelta;
    }
    
    public double getYDelta() {
        return ydelta;
    }

    public void setYDelta(double theYDelta) {
        this.ydelta = theYDelta;
    }
    
    public boolean equals(Object o){
        if (!(o instanceof NodeMovement)){
            return false;
        }
        return ((NodeMovement) o).getNode().equals(node);
    }
}
