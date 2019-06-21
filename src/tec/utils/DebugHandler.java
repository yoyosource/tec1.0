package tec.utils;

import tec.Tec;

public class DebugHandler {
	private DebugLevel level;
	private String message;
	private int time;
	public DebugHandler(DebugLevel level, String message) {
		this.level = level;
		this.message = message;
		this.time = -1;
	}
	public DebugHandler(DebugLevel level, int time) {
		this.level = level;
		this.time = time;
		this.message = "Message was undefined";
	}
	public DebugHandler(DebugLevel level, String message, int time) {
		this.level = level;
		this.message = message;
		this.time = time;
	}
	public void send() {
		if (Tec.debug.equals(DebugLevel.NONE)) return;
		else if (Tec.debug.equals(DebugLevel.NORMAL) && level.equals(DebugLevel.NORMAL)) {
			if (time >= 0) System.out.println(message + "\nBuild time: " + time);
			else System.out.println(message + "\nBuild time unspecified.");
		}
		else if (Tec.debug.equals(DebugLevel.NORMAL) && level.equals(DebugLevel.NORMAL)) {
			if (time >= 0) System.out.println(message + "\nBuild time: " + time);
		}
	}
}
