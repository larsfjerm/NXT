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
	private double kalmanGain = 0.1;  // Tuningparameter for observer
	private double observerAdjust = 0;	

	public double Observer(double dbeta,double psi){
		double d_h  = 0.0450; 
//		double d_b  = 0.0510;
		double l_h  = 0.11;
		double V 	= 0;
		double dt   = 0.1;

		double u = dbeta;
		double B = (d_h/l_h);
		double A = -V/l_h;

		double dpsi_hat = A*last_psi*dt+B*dt*u+observerAdjust;
		last_psi = last_psi+dpsi_hat;
		observerAdjust = (psi-last_psi)*kalmanGain;
		
		return last_psi;
	}

	public void run(){
		NXTRegulatedMotor hitch = car.getHitch();
		setHitchDegPerSec(hitch, dbeta);
		for(int i = 0; i<2;i++){
			hitch.backward();
			dbeta = 10;
			while(hitch.getTachoCount()>=HITCH_RIGHT_LIMIT){
				log();
			}
			hitch.forward();
			dbeta = -10;
			while(hitch.getTachoCount()<=HITCH_LEFT_LIMIT){
				log();
			}
		}
		hitch.rotateTo(0);
		Button.waitForAnyPress();
	}

	private void setHitchDegPerSec(NXTRegulatedMotor hitch, float hitchDegSec){
		final int gear1 = 16;
		final int gear2 = 56;

		float motorDegSec = (gear2/gear1)*hitchDegSec;		//motor
		System.out.println(motorDegSec);
		hitch.setSpeed(motorDegSec);
	}

	private void log(){
		double psi = car.getHitchAngle() - car.getTrailerAngle();
		double beta = car.getHitchAngle();
		double psi_hat = Observer(dbeta, psi);

		System.out.println(psi);
		
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
		logger.finishLine();
	}

	public static void main(String[] args) {
		System.out.println("Waiting for connection...");
		USBConnection link = USB.waitForConnection();
		System.out.println("Connected.");
		DataOutputStream dataOut = link.openDataOutputStream();

		HitchTester t = new HitchTester(dataOut);
		
		Button.waitForAnyPress();
		t.run();
	}

}
