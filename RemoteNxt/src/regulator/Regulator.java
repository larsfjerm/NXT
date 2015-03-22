package regulator;

import java.io.DataOutputStream;

import log.Logger;
import car.Car;

public class Regulator extends Thread{
	private Car car;
	private Logger logger;
	private static double last_psi= 0;
	private static double kp = 2;
	private static double kd = 0;
	
	public Regulator(Car car, DataOutputStream dataOut){
		this.car = car;
		logger = new Logger(dataOut);
	}

	public double getBetaPropotional(double psi){
		
		double psiRef = 0;
		double errorPsi = psi-psiRef;
		return errorPsi;
	}

	public double getBetaDerivative(double psi){
		double dpsi = (psi-last_psi/0.1);
		return dpsi;
	}
	
	
	public void regulate(){
		double psiDeg = car.getHitchAngle() - car.getTrailerAngle();
		double psiRad = psiDeg*Math.PI/180;
		double alphaDeg =  car.getTurnAngle();
		double alphaRad = alphaDeg*Math.PI/180;
		
		double dBetaP = getBetaPropotional(psiRad);
		double dBetaD = getBetaDerivative(psiRad);
		 
		
		double dBetaDeg = (kp*dBetaP+kd*dBetaD)*180/Math.PI;
		
		car.setHitchDegPerSec((float)dBetaDeg);
		log(psiDeg,dBetaP,dBetaD,dBetaDeg);
	}

	private void log(double psi, double dBetaP, double dBetaD, double dBeta){
		if(logger!=null){
			logger.writeDouble(psi);
			logger.writeDouble(dBetaP);
			logger.writeDouble(dBetaD);
			logger.writeDouble(dBeta);
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
