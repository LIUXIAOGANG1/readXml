package net.sourceforge.jpowergraph.example.nodes;

import net.sourceforge.jpowergraph.ClusterNode;
import net.sourceforge.jpowergraph.defaults.DefaultNode;

/**
 * @author Mick Kerrigan
 *
 * Created on 03-Aug-2005
 * Committed by $Author: morcen $
 *
 * $Source: /cvsroot/jpowergraph/common/example/net/sourceforge/jpowergraph/example/nodes/NodeType4.java,v $,
 * @version $Revision: 1.1 $ $Date: 2007/05/20 17:47:57 $
 */

public class NodeType4 extends DefaultNode implements ClusterNode{

    private String label = "";
    private int size =0;
    
    public NodeType4(String theLabel, int theSize){
        this.label = theLabel; 
        this.size = theSize;
    }
    
    public String getLabel() {
        return label;
    }
    
    public boolean showLabel() {
        return true;
    }
    
    public int size(){
        return size;
    }
    
    public String getNodeType(){
        return "Node Type 4 (Cluster Node)";
    }
    
    public boolean equals(Object obj){
        if (!(obj instanceof NodeType4)){
            return false;
        }
        NodeType4 otherNode = (NodeType4) obj;
        return otherNode.getLabel().equals(label);
    }
}
