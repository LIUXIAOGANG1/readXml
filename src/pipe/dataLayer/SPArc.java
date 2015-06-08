package pipe.dataLayer;

public class SPArc {
	int transNo;
	int from ;
	int to;
	
	public SPArc(){
		
	}
	
	public SPArc(int _from, int _to, int _transNo){
		transNo = _transNo;
		from = _from;
		to = _to;
	}
	public void setFromTo(int _from , int _to){
		from = _from ;
		to = _to;
	}
	// ”√”⁄øΩ±¥
	public SPArc copy(){
		SPArc arc = new SPArc(from , to , transNo);
		return arc;
		
		
	}
	public int getFrom(){
		return from;
	}
	public int getTo(){
		return to;
	}
	public int getTransNo(){
		return transNo;
	}
	

}
