package regulator;

import java.io.DataOutputStream;

import log.Logger;
import car.Car;

public class Regulator extends Thread{
	private Car car;
	private Logger logger;
	private static double last_psi= 0;
	
	
	public Regulator(Car car, DataOutputStream dataOut){
		this.car = car;
		logger = new Logger(dataOut);
	}

	public double getBeta(double alpha, double psi){
		double kp = 2;
		double kd = 0;

		double psiRef;
		double dpsi = (psi-last_psi/0.1);
		
		psiRef = 0;
		
		double errorPsi = psi-psiRef;
		return kp*errorPsi+kd*dpsi;
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
			regulate();
			
//			if(car.isMovivingBackward()){
//				regulate();
//			}
//			else if(car.isMovingForward()){
//				car.turnHitchTo(0);
//			}
		}
	}
}
