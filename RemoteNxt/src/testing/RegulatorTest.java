package testing;

import regulator.Regulator;
import lejos.nxt.Button;
import lejos.nxt.Sound;
import car.Car;

public class RegulatorTest{
	public static void main(String[] args) {
		Car c = new Car();
		Regulator r = new Regulator(c,null);
		r.start();
		System.out.println("Press button to start.");
		Button.waitForAnyPress();
		c.moveBackward();
	}
}
