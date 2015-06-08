package pipe.dataLayer;

import java.util.ArrayList;


public class Pathset {
	
	
	public ArrayList <Path> pathSet = new ArrayList <Path>(2);
	
	public Pathset(){
		
	}
	public Pathset(ArrayList <Path> ps){
		pathSet = ps;
	}
	public Pathset(Pathset A){
		for(int i = 0; i < A.pathSet.size(); i++){
			Path a = new Path(A.pathSet.get(i));
			pathSet.add(a);
		}
		//pathSet.addAll(A.pathSet);
		
	}
	public Pathset copy(){
		Pathset A = new Pathset();
		for(int i = 0; i < pathSet.size(); i++){
			Path a = new Path(pathSet.get(i));
			pathSet.add(a);
		}
		return A;
	}
	
	

	
	public void addPath(Path pt){
		pathSet.add(pt);
	}
	
	public void addTransition(String transName){
		for(int i = 0 ; i <pathSet.size() ; i ++){
			pathSet.get(i).PathAddTrans(transName);
		}
	}

}
