package regulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import controller.Command;

public class Regulator{
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	public Regulator(DataInputStream dataIn, DataOutputStream dataOut){
		this.dataIn = dataIn;
		this.dataOut = dataOut;
	}
	
	private double sign(double n){
		if(n==0){
			return 1;
		}
		return n/Math.abs(n);
	}
	
	private double getBeta(double alpha, double psi){
		double kp = 2;
		double lb = 0.162;
		double d = 0.096;
		
		double gamma;
		double psiRef;
		
		double alphaSgn = -1*sign(alpha);
		if(alpha!=0){
			double denom = Math.sqrt(Math.pow(d, 2)+lb/Math.pow(Math.tan(alpha),2));
			gamma = Math.acos(-d/denom);
			psiRef = Math.acos(lb/denom)-gamma;
		}else{
			psiRef = 0;
		}
		psiRef = psiRef*alphaSgn;
		double errorPsi = psi-psiRef;
		return kp*errorPsi;
	}
	
	private boolean receivedAlpha = false;
	private boolean receivedPsi = false;
	private double alpha;
	private double psi;
	
	public boolean checkForUpdate(){
		if(!receivedAlpha){
			try {
				alpha = dataIn.readDouble();
			} catch (IOException e) {
				return false;
			}
			receivedAlpha = true;
		}else if(!receivedPsi){
			try {
				psi = dataIn.readDouble();
			} catch (IOException e) {
				return false;
			}
			receivedPsi = true;
		}
		return true;
	}
	
	public boolean sendUpdate(){
		double beta = getBeta(alpha, psi)*180/Math.PI;
		try {
			dataOut.writeInt(Command.UPDATE_BETA.ordinal());
			dataOut.writeDouble(beta);
			dataOut.flush();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public boolean isReady(){
		return receivedAlpha && receivedPsi;
	}
}
