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
	
	private int backwardSpeed;
	private int forwardSpeed;
	private int turnSpeed;
	private int hitchSpeed;
	
	public Car(){
		backWheels = Motor.C;
		frontWheels = Motor.B;
		hitch = Motor.A;
		angleSensor = new HitchAngleSensor();
		angleSensor.calibrate(hitch);
		
		backwardSpeed = 50;
		forwardSpeed = 200;
		turnSpeed = 100;
		hitchSpeed = 100;
		
		backWheels.setSpeed(forwardSpeed);
		frontWheels.setSpeed(turnSpeed);
		hitch.setSpeed(hitchSpeed);
	}
	
	
	public void setHitchDegPerSec(float hitchDegSec){
		float l1 = (float) 0.8;									//radius motor drev
		float l2 = (float) 2.4;									//radius stort drev
 		float motorDegSec = (l2/l1)*hitchDegSec;		//motor
		
		if(motorDegSec > 0){
			turnHitchLeft();
			hitch.setSpeed(Math.abs(motorDegSec));
		}else if(motorDegSec < 0){
			turnHitchRight();
			hitch.setSpeed(Math.abs(motorDegSec));
		}else{
			hitch.setSpeed(0);
		}
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
		backWheels.setSpeed(forwardSpeed);
		backWheels.forward();
		movingForward  = true;
	}
	
	private boolean movingBackward = false;
	private boolean movingForward  = false;
	
	public void moveBackward(){
		backWheels.setSpeed(backwardSpeed);
		backWheels.backward();
		movingBackward = true;
	}
	
	public boolean isMovivingBackward(){
		return movingBackward;
	}
	
	
	public boolean isMovingForward(){
		return movingForward;
	}
	
	public void resetFront(){
		frontWheels.rotateTo(0);
	}
	
	public void stopMoving(){
		backWheels.flt();
		movingBackward = false;
		movingForward  = false;
	}
	
	
	public void stopTurning(){
		frontWheels.stop();
	}
	
	public void stopHitch(){
		hitch.stop();
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
