package pipe.dataLayer;

public class TimesetOperation {
	public  Timeset Multiple(Timeset A , Timeset B){
		Timeset res = new Timeset();
		int Aint = A.Timeset.size();
		int Bint = B.Timeset.size();
		for(int i = 0; i < Aint ; i ++){
			Duration D1 = new Duration(A.getDuration(i));
			for(int j = 0 ; j < Bint ; j++){
				Duration D2 = new Duration(B.getDuration(j));
				DurOperation dp = new DurOperation();
				res.Timeset.add(dp.DurationMax(D1, D2));
				
			}									
		}
		return res;		
	}
	public Timeset Union(Timeset A , Timeset B){
		Timeset res = new Timeset();
		res.addTimeset(A);
		res.addTimeset(B);
		return res;
	}

}
