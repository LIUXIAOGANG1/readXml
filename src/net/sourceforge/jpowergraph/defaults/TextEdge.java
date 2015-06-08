package net.sourceforge.jpowergraph.defaults;

import net.sourceforge.jpowergraph.Node;

/**
 * The default implementation of the edge.
 */
public class TextEdge extends DefaultEdge {

    private String text;
    
    /**
     * Creates an instance of this class.
     *
     * @param from                  the node from
     * @param to                    the node to
     */
    public TextEdge(Node from, Node to, String theText) {
        super(from, to);
        this.text = theText;
    }
    
    public String getText(){
        return text;
    }
    
    public void setText(String theText){
        this.text = theText;
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof TextEdge)){
            return false;
        }
        if (!super.equals(obj)){
            return false;
        }
        TextEdge otherEdge = (TextEdge) obj;
        return otherEdge.getText().equals(getText());
    }
}
