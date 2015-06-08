package net.sourceforge.jpowergraph.example.nodes;

import net.sourceforge.jpowergraph.defaults.DefaultNode;

/**
 * @author Mick Kerrigan
 *
 * Created on 03-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/common/example/net/sourceforge/jpowergraph/example/nodes/NodeType3.java,v $,
 * @version $Revision: 1.1 $ $Date: 2006/10/26 18:35:39 $
 */

public class NodeType3 extends DefaultNode{

    private String label = "";
    
    public NodeType3(String theLabel){
        this.label = theLabel; 
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getNodeType(){
        return "Node Type 3";
    }
    
    public boolean equals(Object obj){
        if (!(obj instanceof NodeType3)){
            return false;
        }
        NodeType3 otherNode = (NodeType3) obj;
        return otherNode.getLabel().equals(label);
    }
}
