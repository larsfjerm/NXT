package regulator;

import java.io.DataOutputStream;

import log.Logger;
import car.Car;

public class Regulator extends Thread{
	private Car car;
	private Logger logger;
	
	private static double startTime;
	private static double currentTime;

	private static double k1 = -11.537;//-5.2711;//-6.9821;
	private static double k2 = 1.6495;//0.2028;//0.5088;

	public Regulator(Car car, DataOutputStream dataOut){
		this.car = car;
		logger = new Logger(dataOut);
	}

	public double getdBeta(double psi,double beta){
		return psi*k1+beta*k2;
	}

	private double last_psi= 0;
	private double kalmanGain = 0.2;  // Tuningparameter for observer
	private double observerAdjust = 0;	
	private double dBetaDeg = 0;

	public double Observer(double dbeta, double psi){
		//Skal fŒ inn vinkelene i grader. 
		
		double A = 1.2749;
		double B = 0.1829;
		
		// Utregning av tilpassning til sensorverder
		observerAdjust = (psi-last_psi)*kalmanGain;
		// Finner neste psi
		last_psi = A*last_psi+B*dbeta+observerAdjust;
		
		return last_psi;
	}
	
	public void regulate(){
		double psiDeg = car.getPsi() - 3.2;
		double psiRad = (psiDeg)*Math.PI/180;

		double betaDeg = car.getHitchAngle();
		double betaRad = betaDeg*Math.PI/180;
		
		double psiHat = Observer(dBetaDeg, psiDeg);
		
		dBetaDeg = getdBeta(psiRad,betaRad)*180/Math.PI;
		
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
			if(dt >= 400){
				System.out.println(dt);
				prevStartTime = System.currentTimeMillis();
				if(car.isMovivingBackward())
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
