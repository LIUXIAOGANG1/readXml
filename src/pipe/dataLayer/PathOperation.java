package pipe.dataLayer;

public class PathOperation {
	public PathOperation(){
		
	}
	
public	Path pathUnion(Path A, Path B){
		Path result = new Path();
		StringBuilder b = A.getPath().append("||").append(B.getPath().toString());
		b.insert(0, "(").append(")");
		result.setPath(b);
		result.setTrans(A);// may be a problem
		return result;
	}

public Path pathInterc(Path A, Path B){
	Path result = new Path ();
	
	StringBuilder b = A.getPathCopy().append("(").append(B.getPath().toString()).append(")");
	result.setPath(b);
	result.setTrans(A); // may be a problem
	return result;
}

}
