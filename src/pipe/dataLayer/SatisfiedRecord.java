package pipe.dataLayer;

public class SatisfiedRecord {
	//int tcNo;
	private Path path1;
	private Path path2;
	private boolean sameProcess;
	
	public SatisfiedRecord(Path _path1 , Path _path2 ,boolean same){
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
			s.append("There's no violation on path ").append(this.getPath2());
		}
		else{
			s.append("There's no vioaltion on paths " ).append(this.getPath1());
			s.append(" and ").append(this.getPath2());
		}
		return s.toString();
	}
	

}
