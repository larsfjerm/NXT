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
import java.util.HashMap;
import java.util.Map;

import car.Car;
import lejos.nxt.*;
import lejos.nxt.comm.*;


public class Controller{ 
	private DataOutputStream dataOut; 
	private DataInputStream dataIn;
	private boolean[] command = new boolean[6];
	
	private Car car;

	public Controller(){
		connect();
	}

	public void checkCommand(){

		try {
			Command c = Command.get(dataIn.readInt());

			switch(c){
			case FORWARD:
				command[0] = true;
				break;
			case FORWARD_STOP:
				command[0] = false;
				break;
			case BACKWARD:
				command[1] = true;
				break;
			case BACKWARD_STOP:
				command[1] = false;
				break;
			case LEFT:
				command[2] = true;
				break;
			case LEFT_STOP:
				command[2] = false;
				break;
			case RIGHT:
				command[3] = true;
				break;
			case RIGHT_STOP:
				command[3] = false;
				break;
			case STOP:
				command[0] = false;
				command[1] = false;
				command[2] = false;
				break;
			case ACCELERATE:
				command[4] = true;
				break;
			case ACCELERATE_STOP:
				command[4] = false;
				break;
			case BRAKE:
				command[5] = true;
				break;
			case BRAKE_STOP:
				command[5] = false;
				break;
			}
		}
		catch (IOException ioe) {
			System.out.println("IO Exception readInt");
		}

	}
	
	public void executeCommands(){
		updateAcceleration();
		updateMove();
		updateTurn();
	}
	
	private void updateAcceleration() {
		if(command[4]){
			car.accelerate();
			command[4] = false;
		}
		if(command[5]){
			car.brake();
			command[5] = false;
		}
		
	}
	
	private void updateMove() {
		if(command[0]){
			car.moveForward();
		}else if(command[1]){
			car.moveBackward();
		}else{
			car.stop();
		}	
	}

	private void updateTurn() {
		if(command[2]){
			car.turnLeft();
		}else if(command [3]){
			car.turnRight();
		}
	}

	public void connect(){  
		System.out.println("Waiting for connection...");
		BTConnection BTLink = Bluetooth.waitForConnection();    
		dataOut = BTLink.openDataOutputStream();
		dataIn = BTLink.openDataInputStream();
	}
	
	public void init(){
		car = new Car();
	}
	
	public void run(){
		while(true){
			checkCommand();
			executeCommands();
		}
	}
	
	public static void main(String[] args) {
		Controller c = new Controller();
		c.connect();
		c.run();
	}
}

