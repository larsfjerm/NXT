package controller;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.*;

import javax.swing.*;

import regulator.Regulator;
import lejos.pc.comm.NXTConnector;

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
	
//	private Regulator r;
	
	public Controller(){ 
		super("Car controller");
		connect();
		init();
	}
	
//	public void run(){
//		while(true){
//			if(!r.checkForUpdate())
//				break;
//			if(r.isReady()){
//				if(!r.sendUpdate())
//					break;
//			}
//		}
//	}

	public void init(){
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(this);
		pack();
		setVisible(true);
//		r = new Regulator(dataIn, dataOut);
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

		if (!link.connectTo("btspp://"))
		{
			System.out.println("\nNo NXT find using bluetooth");
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
		if(e.getKeyChar() == 'a')
			sendCommand(Command.HITCH_LEFT);
		if(e.getKeyChar() == 'd')
			sendCommand(Command.HITCH_RIGHT);
	}

	private void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode()==KeyEvent.VK_DOWN)
			sendCommand(Command.STOP_MOVE);
		if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_RIGHT)
			sendCommand(Command.STOP_TURN);
		if(e.getKeyChar() == 'a' || e.getKeyChar() == 'd')
			sendCommand(Command.STOP_HITCH);
	}


	public void disconnect()
	{
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

	public static void main(String[] args){
		Controller c = new Controller();
//		c.run();
	}
}