package regulator;

import java.io.DataOutputStream;

import log.Logger;
import car.Car;

public class Regulator extends Thread{
	private Car car;
	private Logger logger;
	
	private static double startTime;
	private static double currentTime;

	private static double k1 = 9.8716;
	private static double k2 = -0.83;

	public Regulator(Car car, DataOutputStream dataOut){
		this.car = car;
		logger = new Logger(dataOut);
	}

	public double getdBeta(double psi,double beta){
		return psi*k1+beta*k2;
	}

	private double last_psi= 0;
	private double kalmanGain = 0.1;  // Tuningparameter for observer
	private double observerAdjust = 0;	

	public double Observer(double dbeta, double psi){
		//Skal få inn vinkelene i grader. 
		
		double boolV = 0; //Enten null eller en
		// Tilsvarer en hastighet V = -0.136 som er motorspeed = 200
		double A = 1.2364;
		double B = -0.0435;
		
		// Utregning av tilpassning til sensorverder
		observerAdjust = (psi-last_psi)*kalmanGain;
		// Finner neste psi
		last_psi = last_psi+B*dbeta+observerAdjust;
		return last_psi;
	}

	public void regulate(){
		double psiDeg = car.getPsi();
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
			currentTime = (System.currentTimeMillis()-startTime)*0.001;
			logger.writeDouble(currentTime);
			logger.writeDouble(psi);
			logger.writeDouble(beta);
			logger.writeDouble(dbeta);
			logger.finishLine();
		}
	}

	public void run(){
		startTime = System.currentTimeMillis();
		log(car.getPsi(), car.getHitchAngle(), 0);
		while(true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			car.checkHitchLimit();
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
