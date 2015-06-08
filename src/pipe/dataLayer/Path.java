package pipe.dataLayer;

import java.util.ArrayList;

public class Path {

	StringBuilder path = null;
	ArrayList<String> transitions = new ArrayList<String>(10);
	
	public Path(){
		
	}
	public Path(String s , ArrayList <String> t){
		this.path = new StringBuilder(s);
		transitions = t;
		
	}
	public Path(StringBuilder strB){
		path =strB;
	}
	public Path(Path p){
		path = new StringBuilder(p.getPath());
		transitions.clear();
		transitions.addAll(p.getTransitions());
	}
	// 如是空字符表明还未加入变迁 不加-分隔符
	public void PathAddTrans(String transitionName){
		if(path.length() != 0){
			path.append("-").append(transitionName);
			if(!transitions.contains(transitionName)){
				transitions.add(transitionName);
				
			}
		}
		else{
			path.append(transitionName);
			if(!transitions.contains(transitionName)){
				transitions.add(transitionName);
				
			}
			
		}
		
	}
	
	public StringBuilder getPath(){
		return path;
	}
	public StringBuilder getPathCopy(){
		StringBuilder a = new StringBuilder(path);
		return a;
		
	}
	
	public void setPath(StringBuilder builder){
		this.path = builder;
	}
	public void setTransitions(ArrayList<String> t){
		this.transitions = t;
	}
    public void setTrans(Path a){
    	this.transitions = a.getTransitions();
    }
	
	
	public ArrayList<String> getTransitions(){
		return transitions;
	}
	/*
	public String getSolutions(ArrayList<String> Process){
		
		
	}
	*/
}
