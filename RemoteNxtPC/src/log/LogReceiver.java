package log;

import java.io.DataInputStream;
import java.io.IOException;

public class LogReceiver extends Thread{
	private DataInputStream dataIn;
	private String log = "";
	
	public LogReceiver(DataInputStream dataIn){
		this.dataIn = dataIn;
	}
	
	public String getLog(){
		return log;
	}
	
	private void checkForUpdate(){
		try {
			log += dataIn.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(true){
			checkForUpdate();
		}
	}
}
