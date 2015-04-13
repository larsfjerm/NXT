package testing;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import car.Car;

public class BetaAngleTester {
	public static void main(String[] args) {
		Car c = new Car();
		Motor.A.rotateTo(120);
		System.out.println(c.getHitchAngle());
		Button.waitForAnyPress();
	}
}
