package testing;

import car.Car;

public class HitchAngleTester {
	public static void main(String[] args) {
		Car c = new Car();
		while(true){
			System.out.println(c.getTrailerAngle());
		}
	}
}
