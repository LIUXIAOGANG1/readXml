package pipe.dataLayer;

public class PathsetOperation {
	public PathsetOperation(){
		
	}
	// µÑ¿¨¶û²¢
	public  Pathset CartUnion(Pathset A, Pathset B){
		Pathset result = new Pathset();
		
		int a = A.pathSet.size();
		int b = B.pathSet.size();
		PathOperation po = new PathOperation();
		
		for (int i = 0; i < a; i ++){
			Path pha = A.pathSet.get(i);
		
			for (int j = 0 ; j < b ; j++)
			{
				Path phb = B.pathSet.get(j);
				Path ph = po.pathUnion(pha, phb);
				result.pathSet.add(ph);
			}
				
			}
		return result;
	}
	public  Pathset CartInterc(Pathset A , Pathset B){
		Pathset result = new Pathset();
		int a = A.pathSet.size();
		int b = B.pathSet.size();
		PathOperation po = new PathOperation();
		
		for (int i = 0; i < a; i ++){
			Path pha = A.pathSet.get(i);
		
			for (int j = 0 ; j < b ; j++)
			{
				Path phb = B.pathSet.get(j);
				Path ph = po.pathInterc(pha, phb);
				result.pathSet.add(ph);
			}
				
			}
		return result;
		
	}
	public Pathset Merge(Pathset A , Pathset B){
		Pathset res = new Pathset();
		res.pathSet.addAll(A.pathSet);
		res.pathSet.addAll(B.pathSet);
		return res;
		
	}
	
	

}
