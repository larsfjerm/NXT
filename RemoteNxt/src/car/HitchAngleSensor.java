package car;

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
		numSteps = 20;
		angles = 60;
		stepLength = (maxAngl-minAngl)/(numSteps*2);
		result = new float[numSteps*2+1];
		s = new LightSensor(SensorPort.S1);
	}
	
	public void calibrate(NXTRegulatedMotor hitch){		
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
	
	public int find(float x){		
	    int low = 0;
	    int high = result.length - 1;

	    while (low < high) {
	        int mid = (low + high) / 2;
	        assert(mid < high);
	        float d1 = Math.abs(result[mid  ] - x);
	        float d2 = Math.abs(result[mid+1] - x);
	        if (d2 <= d1){
	            low = mid+1;
	        }else{
	            high = mid;
	        }
	    }
	    return high;
	}
	
	public int find2(float x){
		int i = numSteps/2;
		if(x > result[i]){
			while(x > result[i]){
				i--;
			}
		}else{
			while(x < result[i]){
				i++;
			}
		}
		return i;
	}
	
	public double getAngle(boolean interpolation){
		float x = getLightValue(0);
		float r = (float)angles/(float)numSteps;
		
		int i = find2(x);
		int j;
		
		if(x > result[i] && interpolation){
			j = i - 1;
		}else if(x < result[i] && interpolation){
			j = i + 1;
		}else{
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
