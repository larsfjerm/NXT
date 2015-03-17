package regulator;

import car.Car;

public class Regulator extends Thread{
	private Car car;
	
	public Regulator(Car car){
		this.car = car;
	}
	
	private double sign(double n){
		if(n==0){
			return 1;
		}
		return n/Math.abs(n);
	}
	
	public double getBeta(double alpha, double psi){
		double kp = 2;
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
		psiRef = psiRef*alphaSgn;
		double errorPsi = psi-psiRef;
		return kp*errorPsi;
	}
	
	public void regulate(){
		double psi = (car.getHitchAngle() - car.getTrailerAngle())*Math.PI/180;
		double alpha =  (car.getTurnAngle())*Math.PI/180;
		double beta = getBeta(alpha, psi)*180/Math.PI;
		car.turnHitchTo(beta);
	}
	
	public void run(){
		while(true){
			if(car.isMovivingBackward()){
				regulate();
			}
		}
	}
}
