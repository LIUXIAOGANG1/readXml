package net.sourceforge.jpowergraph.defaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.NodeFilter;
import net.sourceforge.jpowergraph.NodeFilterListener;

public class DefaultNodeFilter implements NodeFilter {
    public static final int VISIBLE = 0;
    public static final int INVISIBLE = 1;
    
    private Map <Class, Integer> states = new HashMap <Class, Integer> ();
    private Map <Class, List<Class>> dependancies = new HashMap <Class, List<Class>> ();
    private List <NodeFilterListener> nodeFilterListeners = new ArrayList <NodeFilterListener> ();
    
    private List <Class> invisibleClasses = new ArrayList <Class> (); // Cache
    private List <Class> invisibleClassesDueToDependances = new ArrayList <Class> (); // Cache
    
    public boolean isNodeVisible(Node theNode, Graph theGraph){
        boolean visible = getFilterState(theNode.getClass()) && theGraph.getAllNodes().contains(theNode); 
        if (visible && theNode.visibleOnlyIfHasOutgoingEdge()){
            visible = false;
            for (Edge e : theNode.getEdgesFrom()){
                if (isNodeVisible(e.getTo(), theGraph)){
                    visible = true;
                    break;
                }
            }
        }
        if (visible && theNode.visibleOnlyIfHasIncomingEdge()){
            visible = false;
            for (Edge e : theNode.getEdgesTo()){
                if (isNodeVisible(e.getFrom(), theGraph)){
                    visible = true;
                    break;
                }
            }
        }
        return visible;
    }
    
    public boolean isEdgeVisible(Edge theEdge, Graph theGraph){
        return isNodeVisible(theEdge.getFrom(), theGraph) && isNodeVisible(theEdge.getTo(), theGraph);
    }
    
    public boolean canChangeFilterState(Class theClass) {
        return states.containsKey(theClass) && !invisibleClassesDueToDependances.contains(theClass);
    }
    
    public void setFilterState(Class theClass, boolean isVisible){
        if (!canChangeFilterState(theClass)){
            return;
        }
        if (isVisible){
            states.put(theClass, VISIBLE);
        }
        else{
            states.put(theClass, INVISIBLE);
        }
        
        invisibleClasses = calculateInvisibleClasses();
        invisibleClassesDueToDependances = calculateInvisibleClassesDueToDependancies();
        fireNodeFilterListener();
    }

    public boolean getFilterState(Class theClass){
        return !invisibleClasses.contains(theClass);
    }

    public void addFilterable(Class theClass, boolean isVisible){
        states.put(theClass, VISIBLE);
        setFilterState(theClass, isVisible);
    }
    
    public void addDependancy(Class theClass, Class theDependantClass){
        if (!dependancies.containsKey(theClass)){
            dependancies.put(theClass, new ArrayList <Class> ());
        }
        dependancies.get(theClass).add(theDependantClass);
    }
    
    public void applyNodeFilter(){
        for (Class c : states.keySet()){
            setFilterState(c, states.get(c) == VISIBLE);
        }
    }
    
    // Node Filter Listener related Methods
    public void addNodeFilterListener(NodeFilterListener theNodeFilterListener){
        this.nodeFilterListeners.add(theNodeFilterListener);
    }
    
    public void removeNodeFilterListener(NodeFilterListener theNodeFilterListener){
        this.nodeFilterListeners.remove(theNodeFilterListener);
    }
    
    private void fireNodeFilterListener() {
        for (NodeFilterListener nfl : nodeFilterListeners){
            nfl.filterStateChanged();
        }
    }
    
    // Methods for calculating the Cache
    private List <Class> calculateInvisibleClasses() {
        List <Class> result = new ArrayList <Class> ();
        for (Class c : states.keySet()){
            if (states.get(c) != VISIBLE){
                result.add(c);
            }
        }
        result.addAll(calculateInvisibleClassesDueToDependancies());
        return result;
    }
    
    private List <Class> calculateInvisibleClassesDueToDependancies() {
        List <Class> result = new ArrayList <Class> ();
        for (Class c : states.keySet()){
            if (states.get(c) != VISIBLE){
                result.addAll(getDependancies(c, new ArrayList <Class> ()));
            }
        }
        return result;
    }
    
    private List <Class> getDependancies(Class theClass, List <Class> theCurrentDependancies){
        if (dependancies.containsKey(theClass)){
            for (Class dep : dependancies.get(theClass)){
                if (!theCurrentDependancies.contains(dep)){
                    theCurrentDependancies.add(dep);
                    theCurrentDependancies.addAll(getDependancies(dep, theCurrentDependancies));
                }
            }
        }
        return theCurrentDependancies;
    }
}