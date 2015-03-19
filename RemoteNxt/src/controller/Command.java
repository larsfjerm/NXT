package controller;

public enum Command {
	FORWARD, 
	BACKWARD,
	LEFT,
	RIGHT,
	STOP_MOVE,
	STOP_TURN;
	
	public static Command get(int ordinal){
		if(ordinal<Command.values().length)
			return Command.values()[ordinal];
		return null;
	}
}