package car;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;

public class HitchAngleSensor {
	private int minAngl;
	private int maxAngl;
	private int degrees;
	private int step;
	
	private float[] result;
	private LightSensor s;

	public HitchAngleSensor(){
		minAngl = -120;
		maxAngl = 120;
		degrees = 40;
		step = (maxAngl-minAngl)/degrees;
		result = new float[degrees+1];
		s = new LightSensor(SensorPort.S1);
	}
	
	public void calibrate(NXTRegulatedMotor hitch){
		System.out.println("Press button to start hitch sensor calibration.");
		Button.waitForAnyPress();
		hitch.rotateTo(minAngl);
		s.calibrateLow();
		sleep(200);
		hitch.rotateTo(maxAngl);
		s.calibrateHigh();
		for(int i = 0; i <= degrees; i++){
			result[i] = getLightValue();
			hitch.rotateTo(maxAngl-i*step);
		}
		hitch.rotateTo(0);
		System.out.println("Hitch sensor calibration done.");
	}

	private float getLightValue(){
		int sum = 0;
		for(int i = 0; i < 10; i++){
			sum += s.getLightValue();
			sleep(50);
		}
		return sum/10;
	}
	
	public int getAngle() {
		float[] a = result;
		float x = getLightValue();
		
	    int low = 0;
	    int high = result.length - 1;

	    while (low < high) {
	        int mid = (low + high) / 2;
	        assert(mid < high);
	        float d1 = Math.abs(a[mid  ] - x);
	        float d2 = Math.abs(a[mid+1] - x);
	        if (d2 <= d1)
	        {
	            low = mid+1;
	        }
	        else
	        {
	            high = mid;
	        }
	    }
	    return degrees/2-high;
	}

	private static void sleep(long ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public float[] getResult(){
		return result;
	}
}
