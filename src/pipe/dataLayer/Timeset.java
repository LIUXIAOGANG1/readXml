package pipe.dataLayer;

import java.util.ArrayList;

public class Timeset {
	public ArrayList <Duration> Timeset = new ArrayList<Duration>(2);
	
	
	public Timeset(){
					
	}
	// 主要是应用于TiEn and TjEn的求解  否则 set(index ,n)会报错 因为指向空
	public Timeset(int capacity){
		for(int i = 0; i <capacity ; i++){
			Duration a = new Duration();
			Timeset.add(a);
		}
	}
	public Timeset(ArrayList <Duration> ts){
		this.Timeset.addAll(ts);
	}
	
	public Timeset(Timeset ts){
		 Timeset B = new Timeset();
		 for(int i = 0; i<ts.Timeset.size(); i++){
			 Duration a = new Duration(ts.Timeset.get(i));
			 B.Timeset.add(a);
		 }
		 Timeset = B.Timeset;
		
	}
	public Timeset copy(){
		Timeset B = new Timeset();
		//B.PlaceNo = PlaceNo;
		for(int i = 0; i <this.Timeset.size() ; i++){
			Duration a = new Duration(this.Timeset.get(i));
			B.Timeset.add(a);
		}
		//B.Timeset.addAll(this.Timeset);
		return B;
	}
	// 注意可重写返回值为String
	//public int getPlaceNo(){
	//	return PlaceNo;
	//}
	public void addDuration(Duration e){
		this.Timeset.add(e);
	}
	public void plusDuration(Duration e){
		DurOperation DO = new DurOperation();
		for(int i = 0; i < Timeset.size(); i++){
			DO.DurationPlus(Timeset.get(i), e);
		}
	}
	public void setDuration(int index ,Duration e){
		this.Timeset.set(index, e);
		
	}
	
	public Duration getDuration(int i){
		return Timeset.get(i);
	}
    public void addTimeset(Timeset A){
    	
    	for(int i = 0 ; i< A.Timeset .size();i++){
    		Duration a = new Duration(A.Timeset.get(i));
    		Timeset.add(a);
    	}
    }
}
