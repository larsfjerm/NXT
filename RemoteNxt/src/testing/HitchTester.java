package testing;

import java.io.DataOutputStream;

import lejos.nxt.Button;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.comm.USB;
import lejos.nxt.comm.USBConnection;
import log.Logger;
import car.Car;

public class HitchTester {
	private static final int HITCH_LEFT_LIMIT = 120;
	private static final int HITCH_RIGHT_LIMIT = -120;

	private double startTime;
	private double currentTime;
	private Car car;
	private Logger logger;

	public HitchTester(DataOutputStream dataOut){
		startTime = 0;
		car = new Car();
		logger = new Logger(dataOut);

	}

	private float dbeta = 10;
	private double last_psi= 0;
	private double kalmanGain = 0.25;  // Tuningparameter for observer
	private double observerAdjust = 0;	

	public double Observer(double dbeta, double psi){
		//Skal få inn vinkelene i grader. 
		
		double boolV = 0; //Enten null eller en
		// Tilsvarer en hastighet V = -0.136 som er motorspeed = 200
		double A = 1.2364*2;
		double B = 0.0871;
		
		// Utregning av tilpassning til sensorverder
		observerAdjust = (psi-last_psi)*kalmanGain;
		// Finner neste psi
		last_psi = last_psi+B*dbeta+observerAdjust;
		return last_psi;
	}

	public void run(){
		NXTRegulatedMotor hitch = car.getHitch();
		
		long prevStartTime = 0;
		long dt;
		
		for(int i = 0; i<2;i++){
			car.setHitchDegPerSec(dbeta);
			while(hitch.getTachoCount()>=HITCH_RIGHT_LIMIT){
				
				dt =  System.currentTimeMillis() - prevStartTime;
				
				if(dt >= 200){
					prevStartTime = System.currentTimeMillis();
					log();
				}
				
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			dbeta = -dbeta;
			car.setHitchDegPerSec(dbeta);
			while(hitch.getTachoCount()<=HITCH_LEFT_LIMIT){
				
				dt =  System.currentTimeMillis() - prevStartTime;
				
				if(dt >= 200){
					prevStartTime = System.currentTimeMillis();
					log();
				}
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			dbeta = -dbeta;
		}
		hitch.rotateTo(0);
		Button.waitForAnyPress();
	}

	private void log(){
		double psi = car.getPsi();
		double sigma = car.getTrailerAngle();
		double beta = car.getHitchAngle();
		double psi_hat = Observer(dbeta, psi);
		
		if(startTime==0){
			startTime = System.currentTimeMillis();
			currentTime = 0;
		}else{
			currentTime = (System.currentTimeMillis()-startTime)*0.001;			
		}

		logger.writeDouble(currentTime);
		logger.writeDouble(psi);
		logger.writeDouble(psi_hat);
		logger.writeDouble(beta);
		logger.writeDouble(dbeta);
		logger.writeDouble(sigma);
		logger.finishLine();
	}

	public static void main(String[] args) {
		System.out.println("Waiting for connection...");
		USBConnection link = USB.waitForConnection();
		System.out.println("Connected.");
		DataOutputStream dataOut = link.openDataOutputStream();

		HitchTester t = new HitchTester(dataOut);
		t.run();
	}

}
