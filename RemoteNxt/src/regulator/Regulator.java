package regulator;

import java.io.DataOutputStream;

import log.Logger;
import car.Car;

public class Regulator extends Thread{
	private Car car;
	private Logger logger;
	
	private static double last_psi= 0;
	private static double kp = 1;			 // Tuning propotional
	private static double kd = 0;			 // Tuning derivative 
	private static double kalmanGain = 0.1;  // Tuningparameter for observer
	private static double observerAdjust = 0;
	
	
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
		last_psi = psi;
		return dpsi;
	}
	
	
//	public double Observer(double dbeta,double psi){
//		double d_h  = 0.0450; 
//	    double d_b  = 0.0510;
//		double l_h  = 0.11;
//		double V 	= -0.1;
//	    double dt   = 0.1;
//		
//		double u = dbeta;
//		double B = (d_h/l_h);
//		double A = -V/l_h;
//		
//		double psi_hat = A*last_psi*dt+B*dt*u+observerAdjust;
//		observerAdjust = (psi-psi_hat)*kalmanGain;
//	}
	
	
	public void regulate(){
		double psiDeg = car.getHitchAngle() - car.getTrailerAngle();
		double psiRad = psiDeg*Math.PI/180;
		
//		double alphaDeg =  car.getTurnAngle();
//		double alphaRad = alphaDeg*Math.PI/180;
		
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
