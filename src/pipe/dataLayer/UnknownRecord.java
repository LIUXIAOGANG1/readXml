package pipe.dataLayer;

public class UnknownRecord {
	//private int tcNo;
	//private int reduced;
	private Path path1;
	private Path path2;
	private boolean sameProcess;
	
	public UnknownRecord(Path _path1 ,Path _path2 ,boolean same){
		//tcNo = _tcNo ;
		path1 = _path1;
		path2 = _path2;
		sameProcess = same;
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
	public String getText(){
		StringBuilder s = new StringBuilder();
		if(sameProcess){
			s.append("Whether there's violation on path ").append(this.getPath2());
		}
		else{
			s.append("Whether there's vioaltion on paths " ).append(this.getPath1());
			s.append(" and ").append(this.getPath2());
		}
		s.append("can not be decided at this moment.");
		return s.toString();
	}
	

}
