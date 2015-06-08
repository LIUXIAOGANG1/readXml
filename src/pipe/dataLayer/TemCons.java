package pipe.dataLayer;

public class TemCons {
       private String from;
       private String to;
       private String time;
     public TemCons(String _from, String _to , String _time){
    	 from = _from;
    	 to = _to;
    	 time = _time;
     }
     public String getFrom(){
    	 return from;
     }
     
     public String getTo(){
    	 return to;
     }
     public String getTime(){
    	 return time;
     }
     public TemCons(TemCons a){
    	 from = a.getFrom();
    	 to = a.getTo();
    	 time = a.getTime();
     }
}
