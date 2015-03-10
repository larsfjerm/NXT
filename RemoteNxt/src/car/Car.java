package car;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class Car {	
	private static final int FRONT_LEFT_LIMIT = 58;
	private static final int FRONT_RIGHT_LIMIT = -58;
	private static final int SPEED_STEP = 50;
	private static final int MAX_SPEED = 1000;
	private static final int HITCH_LEFT_LIMIT = 120;
	private static final int HITCH_RIGHT_LIMIT = -120;
	
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
		
		speed = 500;
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
	
	public boolean checkHitchLimit(){
		if(hitch.getTachoCount() <= HITCH_RIGHT_LIMIT || hitch.getTachoCount() >= HITCH_LEFT_LIMIT){
			stopHitch();
			return true;
		}
		return false;
	}
	
	
	
	public boolean checkTurnLimit(){
		if(frontWheels.getTachoCount()  <= FRONT_RIGHT_LIMIT || hitch.getTachoCount() >= HITCH_LEFT_LIMIT){
			stopTurning();
			return true;
		}
		return false;
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
	
	public void accelerate(){
		if(speed<MAX_SPEED){
			speed+=SPEED_STEP;
			backWheels.setSpeed(speed);
		}
	}
	
	public void brake(){
		if(speed>0){
			speed-=SPEED_STEP;
			backWheels.setSpeed(speed);
		}
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
}
