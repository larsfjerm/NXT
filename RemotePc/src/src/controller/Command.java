package controller;

public enum Command {
	FORWARD, 
	BACKWARD,
	LEFT,
	RIGHT,
	HITCH_LEFT,
	HITCH_RIGHT,
	STOP_MOVE,
	STOP_TURN,
	STOP_HITCH;
	
	public static Command get(int ordinal){
		if(ordinal<Command.values().length)
			return Command.values()[ordinal];
		return null;
	}
}