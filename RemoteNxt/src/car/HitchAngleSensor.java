package car;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;

public class HitchAngleSensor {
	private int minAngl;
	private int maxAngl;
	private int numSteps;
	private int stepLength;
	private int angles;
	
	private float[] result;
	private LightSensor s;

	public HitchAngleSensor(){
		minAngl = -120;
		maxAngl = 120;
		numSteps = 30;
		angles = 49;
		stepLength = (maxAngl-minAngl)/(numSteps*2);
		result = new float[numSteps*2+1];
		s = new LightSensor(SensorPort.S1);
	}
	
	public void calibrate(NXTRegulatedMotor hitch){
//		System.out.println("Press button to start hitch sensor calibration.");
//		Button.waitForAnyPress();
		
		hitch.rotateTo(minAngl);
		sleep(500);
		s.calibrateLow();
		sleep(500);
		hitch.rotateTo(maxAngl);
		sleep(500);
		s.calibrateHigh();
		sleep(500);
		
		for(int i = 0; i <= numSteps*2; i++){
			result[i] = getLightValue(10);
			hitch.rotateTo(maxAngl-i*stepLength);
		}
		hitch.rotateTo(0);
		System.out.println("Hitch sensor calibration done.");
	}

	private float getLightValue(int ms){
		int sum = 0;
		for(int i = 0; i < 10; i++){
			sum += s.getLightValue();
			sleep(ms);
		}
		return sum/10;
	}
	
	public double getAngle(boolean interpolation){
		float x = getLightValue(0);
		float r = (float)angles/(float)numSteps;
		
		int i,j;
		if(x > result[numSteps]){
			i = 0;
			while(x<result[i])
				i++;
			j = i;
			if(i > 0)
				j -= 1;	
		}else{
			i = numSteps*2;
			while(x>result[i])
				i--;
			j = i;
			if(i < numSteps*2)
				j += 1;
		}
		
		if(i==j && !interpolation){
			return i*r-angles;
		}
		
		double y1 = i*r-angles;
		double y2 = j*r-angles;
		double x1 = result[i];
		double x2 = result[j];
		
		if(x2==x1){
			return y1;
		}
		
		return y1+(y2-y1)*(x-x1)/(x2-x1);
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