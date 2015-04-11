package log;

import java.io.DataOutputStream;
import java.io.IOException;

public class Logger {
	private DataOutputStream dataOut;
	private String log = "";
	private int noLines = 0;
	
	public Logger(DataOutputStream dataOut){
		this.dataOut = dataOut;
	}
	
	boolean firstInLine = true;
	
	public void writeDouble(double d){
		if(!firstInLine){
			log += ", ";
		}
		log += d;
		firstInLine = false;
	}
	
	private final int batchSize = 10;
	
	public void finishLine(){
		log += "\r\n";
		firstInLine = true;
		noLines += 1;
		if(noLines == batchSize){
			sendLog();
			log = "";
			noLines = 0;
		}
	}
	
	private void sendLog(){
		try {
			dataOut.writeUTF(log);
			dataOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
