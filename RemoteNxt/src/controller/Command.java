package controller;

public enum Command {
	FORWARD, 
	FORWARD_STOP, 
	BACKWARD,
	BACKWARD_STOP,
	LEFT,
	LEFT_STOP,
	RIGHT,
	RIGHT_STOP,
	ACCELERATE,
	ACCELERATE_STOP,
	BRAKE,
	BRAKE_STOP,
	STOP;
	
	public static Command get(int ordinal){
		for(Command c : Command.values()){
			if(c.ordinal()==ordinal){
				return c;
			}
		}
		return null;
	}
}
