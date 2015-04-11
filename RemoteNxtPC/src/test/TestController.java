package test;

import controller.Command;
import lejos.pc.comm.NXTConnector;
import log.LogReceiver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TestController{
	private  DataOutputStream dataOut;
	private  DataInputStream dataIn;
	private  NXTConnector link;
	private LogReceiver lr;
	
	public TestController(){ 
		connect();
		init();
	}
	
	public void run(){
		lr.start();
	}
	
	public void stop(){	
		lr.stopRunning();
		lr.saveLog();
		disconnect();
	}

	private void init(){
		lr = new LogReceiver(dataIn);
	}
	
	private void connect()
	{
		link = new NXTConnector();
//		if(!link.connectTo("btspp://")){
		if(!link.connectTo("usb://")){
			System.out.println("\nCannot connect to NXT.");
		}else{
			dataOut = new DataOutputStream(link.getOutputStream());
			dataIn = new DataInputStream(link.getInputStream());
			System.out.println("\nNXT is Connected");   			
		}
	}

	private void sendCommand(Command c){
		try{
			System.out.println(c);
			dataOut.writeInt(c.ordinal());
			dataOut.flush(); 
		}catch(IOException e){
			System.out.println("\nIO Exception writeInt");
		}
	}

	public void disconnect(){
		try{
			dataOut.close();
			dataIn.close();
			link.close();
		} 
		catch (IOException ioe) {
			System.out.println("\nIO Exception writing bytes");
		}
		System.out.println("\nClosed data streams");
	}
	
	public static void main(String[] args) {
		TestController tc = new TestController();
		tc.sendCommand(Command.BACKWARD);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}