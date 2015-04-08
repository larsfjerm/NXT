package controller;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.*;

import javax.swing.*;

import lejos.pc.comm.NXTConnector;
import log.LogReceiver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Controller extends JFrame implements KeyEventDispatcher{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  DataOutputStream dataOut;
	private  DataInputStream dataIn;
	private  NXTConnector link;
	private LogReceiver lr;
	
	public Controller(){ 
		super("Car controller");
		connect();
		init();
	}
	
	public void run(){
		lr.start();
	}
	
	private void stop(){	
		lr.stopRunning();
		lr.saveLog();
		disconnect();
	}

	public void init(){
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(this);
		pack();
		setVisible(true);
		lr = new LogReceiver(dataIn);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_PRESSED) {
			keyPressed(e);
		} else if (e.getID() == KeyEvent.KEY_RELEASED) {
			keyReleased(e);
		}
		return false;
	}

	public void connect()
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

	private void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP)
			sendCommand(Command.FORWARD);
		if(e.getKeyCode() == KeyEvent.VK_DOWN)
			sendCommand(Command.BACKWARD);
		if(e.getKeyCode() == KeyEvent.VK_LEFT)
			sendCommand(Command.LEFT);
		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			sendCommand(Command.RIGHT);
		if(e.getKeyChar() == KeyEvent.VK_ESCAPE){
			stop();
		}
	}

	private void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode()==KeyEvent.VK_DOWN)
			sendCommand(Command.STOP_MOVE);
		if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_RIGHT)
			sendCommand(Command.STOP_TURN);
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
}