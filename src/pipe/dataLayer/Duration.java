package pipe.dataLayer;



public class Duration {

	private int starttime;
	private int endtime;
	
	public Duration(int s, int e){
		this.starttime = s;
		this.endtime = e;
		
	}
	public Duration(){
		
	}
	public Duration(Duration d){
		this.starttime = d.getStarttime();
		this.endtime = d.getEndtime();
	}
	public Duration copy(){
		 Duration m = new Duration(this.starttime, this.endtime);
		 return m;
	}
	
	
	public void setStarttime(int s){
		this.starttime = s;
	}
	
    public int getStarttime(){
    	return starttime;
    }
    
    public void setEndtime(int e){
    	this.endtime = e;
    }
    public int getEndtime(){
    	return this.endtime;
    }
}
