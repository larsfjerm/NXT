package testing;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import car.Car;

public class HitchAngleTester {
	public static void main(String[] args) {
		Car c = new Car();
		while(true){
			System.out.println(c.getPsi());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
