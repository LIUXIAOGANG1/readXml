package pipe.dataLayer;

public class CheckRecord {
	private int tcNo;
	private int reduced;
	private Path path1;
	private Path path2;
	private boolean sameProcess;
	//int []  transitions;
	private String solution;
	
	public CheckRecord(int _tcNo, int _reduced, Path _path1,
			Path _path2,boolean _sameProcess ,String _solution ){
		tcNo = _tcNo;
		reduced = _reduced;
		path1 = _path1;
		path2 = _path2;
		sameProcess = _sameProcess;
		solution = _solution;
		//transitions = _transitions;
	}
	public CheckRecord(){
		
	}
	public boolean sameProcess(){
		return sameProcess;
	}
	public String gettcNo(){
		String a =String.valueOf(tcNo);
		return a;
	}
	public int gettcNum(){
		return tcNo;
	}
	
	public String getReduced(){
		String a = String.valueOf(reduced);
		return a;
	}
	
	
	public String getPath1(){
		StringBuilder ph = new StringBuilder("[");
		ph.append(path1.getPath()).append("]");
	    return ph.toString();
		
	}
	public String getPath2(){
		StringBuilder ph = new StringBuilder("[");
		ph.append(path2.getPath()).append("]");
	    return ph.toString();				
	}
	
	public String getSolution(){
		return solution;
	}
	
	public String getText(){
		String s = "The errorneous path may be";
		if(sameProcess){
			s += this.getPath2();			
		}
		else{
			s += this.getPath1();
			s += "and " + this.getPath2();
		}
		s +=". The solution is " + this.getSolution()+ ". ";
		return s;
	}
	
	

}
