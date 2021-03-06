/*
 * Copyright (c) 2005 National University of Ireland, Galway
 *
 * Licensed under MIT License
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated 
 * documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons 
 * to whom the Software is furnished to do so, subject to the 
 * following conditions:
 *
 * The above copyright notice and this permission notice shall 
 * be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES 
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH 
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.sourceforge.jpowergraph.defaults;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.SubGraphHighlighter;


public class DefaultSubGraphHighlighter implements SubGraphHighlighter {

    private Graph graph = null;
    
    public DefaultSubGraphHighlighter() {
    }

    public Graph getSubGraph(){
        return this.graph;
    }
    
    public void setSubGraph(Graph theGraph){
        this.graph = theGraph;
    }
    
    public boolean isHighlightSubGraphs() {
        return graph != null;
    }

    public boolean doesSubGraphContain(Node theNode) {
        if (graph == null){
            return true;
        }
        return graph.getAllNodes().contains(theNode);
    }

    public boolean doesSubGraphContain(Edge theEdge) {
        if (graph == null){
            return true;
        }
        return graph.getAllEdges().contains(theEdge);
    }
}
