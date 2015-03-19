package log;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class LogReceiver extends Thread{
	private DataInputStream dataIn;
	private String log = "";
	private boolean isRunning = true;
	
	public LogReceiver(DataInputStream dataIn){
		this.dataIn = dataIn;
	}
	
	public void saveLog(){
		System.out.println("Filename: ");
		Scanner s = new Scanner(System.in);
		String filename = s.nextLine();
		try {
			PrintWriter pw = new PrintWriter(filename+".csv");
			pw.write("Psi, Alpha, Beta\r\n");
			pw.write(log);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.close();
	}
	
	private void checkForUpdate(){
		try {
			log += dataIn.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopRunning(){
		isRunning = false;
	}
	
	public void run(){
		while(isRunning){
			checkForUpdate();
		}
	}
}
