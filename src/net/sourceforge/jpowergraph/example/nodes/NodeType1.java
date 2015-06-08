package net.sourceforge.jpowergraph.example.nodes;

import net.sourceforge.jpowergraph.defaults.DefaultNode;

/**
 * @author Mick Kerrigan
 *
 * Created on 03-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/common/example/net/sourceforge/jpowergraph/example/nodes/NodeType1.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:35:39 $
 */

public class NodeType1 extends DefaultNode{

    private String label = "";
    
    public NodeType1(String theLabel){
        this.label = theLabel; 
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getNodeType(){
        return "Node Type 1";
    }
    
    public boolean equals(Object obj){
        if (!(obj instanceof NodeType1)){
            return false;
        }
        NodeType1 otherNode = (NodeType1) obj;
        return otherNode.getLabel().equals(label);
    }
}
