package car;

import regulator.Regulator;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

public class Car {	
	private static final int FRONT_LEFT_LIMIT = 30;
	private static final int FRONT_RIGHT_LIMIT = -30;
	private static final int HITCH_LEFT_LIMIT = 120;
	private static final int HITCH_RIGHT_LIMIT = -120;
	private static final int HITCH_RATIO = 90/39;
	
	private NXTRegulatedMotor backWheels;
	private NXTRegulatedMotor frontWheels;
	private NXTRegulatedMotor hitch;
	private HitchAngleSensor angleSensor;
	
	private int speed;
	private int turnSpeed;
	private int hitchSpeed;
	
	public Car(){
		backWheels = Motor.C;
		frontWheels = Motor.B;
		hitch = Motor.A;
		angleSensor = new HitchAngleSensor();
		angleSensor.calibrate(hitch);
		
		speed = 200;
		turnSpeed = 100;
		hitchSpeed = 100;
		
		backWheels.setSpeed(speed);
		frontWheels.setSpeed(turnSpeed);
		hitch.setSpeed(hitchSpeed);
	}
	
	public void turnLeft(){
		if(frontWheels.getTachoCount()<FRONT_LEFT_LIMIT){
			frontWheels.forward();
		}
	}
	
	public void turnRight(){
		if(frontWheels.getTachoCount()>FRONT_RIGHT_LIMIT){
			frontWheels.backward();
		}
	}
	
	public void turnTo(double degrees){
		int intDegrees = (int) Math.ceil(degrees);
		if(intDegrees > FRONT_RIGHT_LIMIT && intDegrees < FRONT_LEFT_LIMIT){
			frontWheels.setSpeed(frontWheels.getMaxSpeed());
			frontWheels.rotateTo(intDegrees);
			frontWheels.setSpeed(turnSpeed);
		}
	}
	
	public void turnHitchTo(double degrees){
		int intDegrees = (int)Math.ceil(degrees*HITCH_RATIO);
		if(intDegrees  > HITCH_RIGHT_LIMIT && intDegrees < HITCH_LEFT_LIMIT){
			hitch.setSpeed(hitch.getMaxSpeed());
			hitch.rotateTo(intDegrees);
			hitch.setSpeed(hitchSpeed);
		}
	}
	
	
	public void turnHitchLeft(){
		if(hitch.getTachoCount()<HITCH_LEFT_LIMIT){
			hitch.forward();
		}
	}
	
	public void turnHitchRight(){
		if(hitch.getTachoCount()>HITCH_RIGHT_LIMIT){
			hitch.backward();
		}
	}
	
	public void checkHitchLimit(){
		if(hitch.getTachoCount() <= HITCH_RIGHT_LIMIT || hitch.getTachoCount() >= HITCH_LEFT_LIMIT){
			stopHitch();
		}
	}
	
	public void checkTurnLimit(){
		if(frontWheels.getTachoCount()  <= FRONT_RIGHT_LIMIT || hitch.getTachoCount() >= HITCH_LEFT_LIMIT){
			stopTurning();
		}
	}
	
	public void moveForward(){
		backWheels.forward();
	}
	
	public void moveBackward(){
		backWheels.backward();
	}
	
	public void resetFront(){
		frontWheels.rotateTo(0);
	}
	
	public void stopMoving(){
		backWheels.flt();
	}
	
	public void stopTurning(){
		frontWheels.stop();
	}
	
	public void stopHitch(){
		hitch.stop();
	}
	
	public void regulate(){
		double psi = (getHitchAngle() - getTrailerAngle())*Math.PI/180;
		double alpha =  (getTurnAngle())*Math.PI/180;
		double beta = Regulator.getBeta(alpha, psi)*180/Math.PI;
		turnHitchTo(beta);
	}
	
	public float getTrailerAngle(){
		return angleSensor.getAngle();
	}
	
	public float getTurnAngle(){
		return frontWheels.getTachoCount();
	}
	
	public float getHitchAngle(){
		return hitch.getTachoCount()/HITCH_RATIO;
	}
	
	public float[] getResult(){
		return angleSensor.getResult();
	}
}
