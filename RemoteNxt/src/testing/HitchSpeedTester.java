package testing;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class HitchSpeedTester {
	private static final int HITCH_LEFT_LIMIT = 120;
	private static final int HITCH_RIGHT_LIMIT = -120;
	private static final int HITCH_RATIO = 90/39;
	
	
	public static void setHitchDegPerSec(NXTRegulatedMotor hitch, float hitchDegSec){
		final int gear1 = 16;
		final int gear2 = 56;
		
 		float motorDegSec = (gear2/gear1)*(float)1.1428*hitchDegSec;		//motor
 		System.out.println(motorDegSec);
 		hitch.setSpeed(motorDegSec);
	}
	
	public static void main(String[] args) {
		Button.waitForAnyPress();
		NXTRegulatedMotor hitch = Motor.A;
		
		hitch.rotateTo(HITCH_LEFT_LIMIT);
		setHitchDegPerSec(hitch, 10);
		System.out.println(hitch.getTachoCount());
		System.out.println(hitch.getPosition());
		Button.waitForAnyPress();
 		hitch.backward();
 		while(hitch.getTachoCount()>=(double)HITCH_RIGHT_LIMIT){
 			System.out.println(hitch.getTachoCount());
 		}
	}
}
