package controller;

import java.io.*;

import regulator.Regulator;
import car.Car;
import lejos.nxt.comm.*;

public class CarController{ 
	private DataOutputStream dataOut; 
	private DataInputStream dataIn;

	private boolean move;
	private boolean turn;
	private Car car;
	private Regulator r;

	public CarController(){
		init();
	}

	public void init(){
		car = new Car();
		move = false;
		turn = false;
		connect();
		r = new Regulator(car, dataOut);
	}

	public void connect(){  
		System.out.println("Waiting for connection...");
		BTConnection link = Bluetooth.waitForConnection();
		//USBConnection link = USB.waitForConnection();
		System.out.println("Connected.");
		dataOut = link.openDataOutputStream();
		dataIn = link.openDataInputStream();
	}

	public void run(){
		r.start();
		//car.moveBackward();
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

