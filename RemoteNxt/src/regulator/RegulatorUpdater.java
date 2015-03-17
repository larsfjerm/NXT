package regulator;

import java.io.DataOutputStream;
import java.io.IOException;

import car.Car;

public class RegulatorUpdater extends Thread{
	private DataOutputStream dataOut; 
	private Car car;

	public RegulatorUpdater(Car car, DataOutputStream dataOut){
		this.dataOut = dataOut;
		this.car = car;
	}

	private boolean sendUpdate(){
		if(car.isMovivingBackward()){
			double alpha =  (car.getTurnAngle())*Math.PI/180;
			double psi = (car.getHitchAngle() - car.getTrailerAngle())*Math.PI/180;
			try {
				dataOut.writeDouble(alpha);
				dataOut.writeDouble(psi);
				dataOut.flush();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}

	public void run(){
		while(true){
			if(!sendUpdate())
				break;
		}
	}

}
