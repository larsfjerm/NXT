package car;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class Car {	
	private static final int FRONT_LEFT_LIMIT = 58;
	private static final int FRONT_RIGHT_LIMIT = -58;
	private static final int TURN_STEP = 2;
	private static final int SPEED_STEP = 50;
	private static final int TRAILER_LEFT_LIMIT = 30;
	private static final int TRAILER_RIGHT_LIMIT = -30;
	private static final int MAX_SPEED = 300;
	
	private NXTRegulatedMotor backWheels;
	private NXTRegulatedMotor frontWheels;
	private NXTRegulatedMotor trailerHitch;
	
	private int speed;
	private int turnSpeed;
	private int frontAngle;
	private int trailerAngle;
	
	public Car(){
		backWheels = Motor.A;
		frontWheels = Motor.C;
		trailerHitch = Motor.B;
		
		speed = 50;
		turnSpeed = 50;
		frontAngle = 0;
		trailerAngle = 0;
		
		backWheels.setSpeed(speed);
		frontWheels.setSpeed(turnSpeed);
	}
	
	public void turnLeft(){
		if(frontAngle<FRONT_LEFT_LIMIT){
			frontAngle += TURN_STEP;
			frontWheels.rotate(TURN_STEP);
		}
	}
	
	public void turnRight(){
		if(frontAngle<FRONT_RIGHT_LIMIT){
			frontAngle -= TURN_STEP;
			frontWheels.rotate(-TURN_STEP);
		}
	}
	
	public void moveForward(){
		backWheels.backward();
	}
	
	public void moveBackward(){
		backWheels.forward();
	}
	
	public void resetFront(){
		frontAngle = 0;
		frontWheels.rotateTo(frontAngle);
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
	
	public void stop(){
		backWheels.stop();
	}
}
