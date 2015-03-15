package regulator;

public class Regulator {
	private static double sign(double n){
		return n/Math.abs(n);
	}
	
	public static double getBeta(double alpha, double psi){
		double kp = 1;
		double lb = 0.162;
		double d = 0.096;
		
		double gamma;
		double psiRef;
		
		double alphaSgn = -1*sign(alpha);
		if(alpha!=0){
			double denom = Math.sqrt(Math.pow(d, 2)+lb/Math.pow(Math.tan(alpha),2));
			gamma = Math.acos(-d/denom);
			psiRef = Math.acos(lb/denom)-gamma;
		}else{
			psiRef = 0;
		}
		psiRef *= alphaSgn;
		double errorPsi = psi-psiRef;
		return kp*errorPsi;
	}
}
