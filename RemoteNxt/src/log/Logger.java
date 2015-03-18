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
	
	public void writeDouble(double d){
		log += d+" ";
	}
	
	public void finishLine(){
		log += '\n';
		noLines += 1;
		if(noLines == 10){
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
