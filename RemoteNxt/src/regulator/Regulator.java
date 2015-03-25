package regulator;

import java.io.DataOutputStream;

import log.Logger;
import car.Car;

public class Regulator extends Thread{
	private Car car;
	private Logger logger;

	private static double last_psi= 0;

//	private static double k1 = -11.4929;			 // Tuning propotional
//	private static double k2 = 1.1609;			 // Tuning derivative 

	private static double k1 = 0;
	private static double k2 = 0;
	
	private static double kalmanGain = 0.1;  // Tuningparameter for observer
	private static double observerAdjust = 0;

	public Regulator(Car car, DataOutputStream dataOut){
		this.car = car;
		logger = new Logger(dataOut);
	}

	public double getdBeta(double psi,double beta){
		return psi*k1+beta*k2;
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

		double betaDeg = car.getHitchAngle();
		double betaRad = betaDeg*Math.PI/180;

		//		double alphaDeg =  car.getTurnAngle();
		//		double alphaRad = alphaDeg*Math.PI/180;

		double dBetaDeg = getdBeta(psiRad,betaRad)*180/Math.PI;

		car.setHitchDegPerSec((float)dBetaDeg);
		log(psiDeg,betaDeg,dBetaDeg);
	}

	private void log(double psi, double beta, double dbeta ){
		if(logger!=null){
			logger.writeDouble(psi);
			logger.writeDouble(beta);
			logger.writeDouble(dbeta);
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
