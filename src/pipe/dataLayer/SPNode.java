package pipe.dataLayer;

public class SPNode {
	// 注意很有可能需要加修饰符控制访问权限
	int PlaceNo;
	Timeset Timeset;
	Pathset Pathset;
	
	public SPNode(){
		
	}
	public SPNode(int p, Timeset t, Pathset ps){
		PlaceNo = p;
		Timeset = t;
		Pathset = ps;
	}
	public SPNode(SPNode A){
		PlaceNo = A.getPlaceNo();
		Timeset = A.getTimeset();
		Pathset = A.getPathset();
		
	}
	// 深度拷贝
	public SPNode deepCopy(){
		SPNode n = new SPNode();
		n.setPlaceNo(PlaceNo);
		n.setPathset(new Pathset(Pathset));
		n.setTimeset(new Timeset(Timeset) );
		return n;
		
		
	}
	public int getPlaceNo(){
		return PlaceNo;
	}
	public void setPlaceNo(int a ){
		PlaceNo = a;
	}
	public Timeset getTimeset(){
		return Timeset;
	}
	public Pathset getPathset(){
		return Pathset;
	}
	public void setTimeset(Timeset c){
		 Timeset = c;// may be a problem
	}
	public void setPathset(Pathset p){
		 Pathset = new Pathset(p);
	}
	
	// used as legend of the node for drawing sprouting graph
	public String getTimesetString(){
		StringBuilder time = new StringBuilder("{");
		for(int i = 0; i < Timeset.Timeset.size(); i++){
			Duration dur = new Duration(Timeset.getDuration(i));
			time.append("[").append(String.valueOf(dur.getStarttime())).append(",");
			time.append(String.valueOf(dur.getEndtime())).append("]").append(",");
		}
		time.deleteCharAt(time.lastIndexOf(","));
		time.append("}");
		return time.toString();
	}
	public String getPathsetString(){
		StringBuilder path = new StringBuilder("{");
		for (int i = 0 ; i < Pathset.pathSet.size() ; i ++){
			Path ph = new Path(Pathset.pathSet.get(i));
			path.append("[").append(ph.getPath()).append("]");
			path.append(",");
		}
		path.deleteCharAt(path.lastIndexOf(","));
		path.append("}");
		return path.toString();
	}
	public String getPlaceNoString(){
		String a = "P" +PlaceNo ;
		return a ;
		//String.valueOf(PlaceNo);
	}

}
