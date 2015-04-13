package car;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

public class Car {	
	private static final int FRONT_LEFT_LIMIT = 30;
	private static final int FRONT_RIGHT_LIMIT = -30;
	private static final int HITCH_LEFT_LIMIT = 120;
	private static final int HITCH_RIGHT_LIMIT = -120;
	private static final float HITCH_RATIO = (56/16)*(float)1.1428;
	
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
		
		// TODO uncomment
		angleSensor.calibrate(hitch);
		
		// TODO remove
		//hitch.rotateTo(0);
		
		backwardSpeed = 100;
		forwardSpeed = 100;
		turnSpeed = 100;
		hitchSpeed = 100;
		
		backWheels.setSpeed(forwardSpeed);
		frontWheels.setSpeed(turnSpeed);
		hitch.setSpeed(hitchSpeed);
	}
	
	public void testHitch() {
		hitch.rotateTo(HITCH_LEFT_LIMIT);
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
	
	public void setHitchDegPerSec(float hitchDegSec){
 		float motorDegSec = HITCH_RATIO*Math.abs(hitchDegSec);		//motor
 		if(motorDegSec>hitch.getMaxSpeed()){
 			Sound.beep();
 			motorDegSec = hitch.getMaxSpeed();
 		}
 		hitch.setSpeed(motorDegSec);
 		
 		if(hitch.isMoving()){
 			hitch.stop();
 		}
 		
		if(hitchDegSec > 0){
			if(hitch.getTachoCount() > HITCH_RIGHT_LIMIT)
				hitch.backward();
		}else{
			if(hitch.getTachoCount() < HITCH_LEFT_LIMIT){
				hitch.forward();			
			}
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
	
	private boolean movingBackward = false;
	private boolean movingForward = false;
	
	public void moveForward(){
		backWheels.setSpeed(forwardSpeed);
		backWheels.forward();
		movingForward = true;
	}
	
	
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
	
	public void stopMoving(){
		backWheels.flt();
		movingForward = false;
		movingBackward = false;
	}
	
	public void stopTurning(){
		frontWheels.stop();
	}
	
	public void stopHitch(){
		hitch.stop();
	}
	
	
	public float getTurnAngle(){
		return frontWheels.getTachoCount();
	}
	
	public float getHitchAngle(){
		return -hitch.getTachoCount()/HITCH_RATIO;
	}
	
	public float getTrailerAngle(){
		return (float)angleSensor.getAngle(false);
	}
	
	public float[] getResult(){
		return angleSensor.getResult();
	}
	
	public float getPsi(){
		return getTrailerAngle() - getHitchAngle();
	}
	
	public NXTRegulatedMotor getHitch(){
		return hitch;
	}
}
