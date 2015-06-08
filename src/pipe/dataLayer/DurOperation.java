package pipe.dataLayer;
import static java.lang.Math.*;
public class DurOperation {
	
	public Duration DurationMax(Duration D1, Duration D2){
		Duration result = new Duration();
		result.setStarttime(max(D1.getStarttime(),D2.getStarttime()));
		result.setEndtime(max(D1.getEndtime(),D2.getEndtime()));
		return result;
	}
	
	public Duration DurationAdd(Duration D1, Duration D2){
		Duration result;
		int s = D1.getStarttime()+ D2.getStarttime();
		int e = D1.getEndtime()+D2.getEndtime();
		result = new Duration(s,e);
		return result;
		
	}
	// ÐÞ¸ÄD1
	public void DurationPlus(Duration D1, Duration D2){
		
		int s = D1.getStarttime()+ D2.getStarttime();
		int e = D1.getEndtime()+D2.getEndtime();
		D1.setStarttime(s);
		D1.setEndtime(e);
		
		
	}
	public boolean Smaller(Duration D1 , Duration D2){
		return (D1.getStarttime() < D2.getStarttime()? true: false);
	}
	

}
