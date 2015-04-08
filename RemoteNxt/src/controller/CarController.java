package controller;


/*
 * September 21, 2009
 * Author by Tawat Atigarbodee
 *
 * Install this program on to NXT brick and use it with NXTremoteControl_TA.java
 *
 * To use this program.
 *  -   Install Lejos 0.8.5
 *  -   Include Lejos_nxj library to the project path
 *  -   Upload the program using lejosdl.bat (I use Eclipse)
 *  -   To exit the program, restart NXT brick (remove battery)
 * 
 * NXT setup
 *  -  Port A for right wheel
 *  -  Port C for left wheel
 *  -  No sensor is needed
 *  
 * Note: This program is a partial of my project file. 
 * I use â€œUSBSendâ€? and â€œUSBReceiveâ€? created by Lawrie Griffiths 
 * as a pattern for creating USB communication between PC and NXT. 
 */

import java.io.*;

import regulator.Regulator;
import car.Car;
import lejos.nxt.comm.*;


public class CarController{ 
	private DataOutputStream dataOut; 
	private DataInputStream dataIn;

	private boolean move;
	private boolean turn;
	private boolean hitchTurn;
	private Car car;
	private Regulator r;

	public CarController(){
		init();
		connect();
		r = new Regulator(car, dataOut);
	}

	public void init(){
		car = new Car();

		move = false;
		turn = false;
		hitchTurn = false;
	
	}

	public void connect(){  
		System.out.println("Waiting for connection...");
//		BTConnection link = Bluetooth.waitForConnection();
		USBConnection link = USB.waitForConnection();
		System.out.println("Connected.");
		dataOut = link.openDataOutputStream();
		dataIn = link.openDataInputStream();
	}

	public void run(){
		r.start();
		while(checkCommand()){}
	}

	public boolean checkCommand(){
		try {
			int value = dataIn.readInt();
			Command c = Command.get(value);
			if(c==null)
				return false;
			if(value < 2)
				move(c);
			else if(value < 4)
				turn(c);
			else
				stop(c);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private void move(Command c){
		if(!move){
			if(c==Command.FORWARD)
				car.moveForward();
			else
				car.moveBackward();
			move = true;
		}
	}

	private void turn(Command c){
		car.checkTurnLimit();

		if(!turn){
			if(c==Command.LEFT)
				car.turnLeft();
			else
				car.turnRight();
			turn = true;
		}
	}

	private void stop(Command c){
		if(move && c==Command.STOP_MOVE){
			car.stopMoving();
			move = false;
		}else if(turn && c==Command.STOP_TURN){
			car.stopTurning();
			turn = false;
		}
	}

	public static void main(String[] args) {
		CarController c = new CarController();
		c.run();
	}
}

