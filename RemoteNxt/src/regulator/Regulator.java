package regulator;

import java.io.DataOutputStream;

import log.Logger;
import car.Car;

public class Regulator extends Thread{
	private Car car;
	private Logger logger;
	
	private static double startTime;
	private static double currentTime;

	private static double k1 = -6.686;
	private static double k2 = 0.382;

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
		//Skal fŒ inn vinkelene i grader. 
		
		double A = 1.0638;
		double B = 0.0422;
		
		// Utregning av tilpassning til sensorverder
		observerAdjust = (psi-last_psi)*kalmanGain;
		// Finner neste psi
		last_psi = A*last_psi+B*dbeta+observerAdjust;
		return last_psi;
	}
	
	public void regulate(){
		double psiDeg = car.getPsi();
		double psiRad = psiDeg*Math.PI/180;

		double betaDeg = car.getHitchAngle();
		double betaRad = betaDeg*Math.PI/180;

		double dBetaDeg = getdBeta(psiRad,betaRad)*180/Math.PI;
		
		double psiHat = Observer(dBetaDeg, psiDeg);
		
		car.setHitchDegPerSec((float)dBetaDeg);
		
		log(psiDeg,psiHat,betaDeg,dBetaDeg);
	}
	
	

	private void log(double psi, double psiHat, double beta, double dbeta ){
		if(logger!=null){
			setCurrentTime();
			logger.writeDouble(currentTime);
			logger.writeDouble(psi);
			logger.writeDouble(psiHat);
			logger.writeDouble(beta);
			logger.writeDouble(dbeta);
			logger.finishLine();
		}
	}
	
	private void setCurrentTime(){
		currentTime = (System.currentTimeMillis()-startTime)*0.001;
	}

	public void run(){
		startTime = System.currentTimeMillis();
		log(car.getPsi(), car.getPsi(), car.getHitchAngle(), 0);
		
		long prevStartTime = 0;
		long dt;
		while(true){
			car.checkHitchLimit();
			dt =  System.currentTimeMillis() - prevStartTime;
			if(dt >= 500){
				System.out.println(dt);
				prevStartTime = System.currentTimeMillis();
				regulate();
			}
			
			//			if(car.isMovivingBackward()){
			//				regulate();
			//			}
			//			else if(car.isMovingForward()){
			//				car.turnHitchTo(0);
			//			}

		}
	}
}
