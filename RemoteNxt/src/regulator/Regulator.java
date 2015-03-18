package regulator;

import java.io.DataOutputStream;

import log.Logger;
import car.Car;

public class Regulator extends Thread{
	private Car car;
	private Logger logger;

	public Regulator(Car car, DataOutputStream dataOut){
		this.car = car;
		logger = new Logger(dataOut);
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
		double psiDeg = car.getHitchAngle() - car.getTrailerAngle();
		double psiRad = psiDeg*Math.PI/180;
		double alphaDeg =  car.getTurnAngle();
		double alphaRad = alphaDeg*Math.PI/180;
		double betaRad = getBeta(alphaRad, psiRad);
		double betaDeg = betaRad*180/Math.PI;
		car.turnHitchTo(betaDeg);
		log(psiDeg,alphaDeg,betaDeg);
	}

	private void log(double psi, double alpha, double beta){
		if(logger!=null){
			logger.writeDouble(psi);
			logger.writeDouble(alpha);
			logger.writeDouble(beta);
			logger.finishLine();
		}
	}

	public void run(){
		while(true){
			if(car.isMovivingBackward()){
				regulate();
			}
		}
	}
}
