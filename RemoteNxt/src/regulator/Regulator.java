package regulator;

public class Regulator {
	
	private static double psi_last = 0;
	
	private static double sign(double n){
		
		if(n == 0){
		return n/Math.abs(n);
		}else{
			return 1;
		}
	}
	
	public static double getBeta(double alpha, double psi){
		double kp = 1;
		double kd = 1;
		double lb = 0.162;
		double d = 0.096;
		double dt = 0.1;
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
		
		
		// calculation of error psi
		double errorPsi = psi-psiRef;
		
		// calculation of error dpsi
		double deltaPsi = (psi-psi_last)/dt;
		psi_last = psi;
		double errorDeltaPsi = deltaPsi-errorPsi;
		
		return kp*errorPsi + kd*errorDeltaPsi;
	}
}
