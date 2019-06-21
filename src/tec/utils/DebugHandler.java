package tec.utils;

public class DebugHandler {
	private String message;
	private int time;
	public DebugHandler(String message) {
		this.message = message;
		this.time = -1;
	}
	public DebugHandler(int time) {
		this.time = time;
		this.message = "Message was undefined";
	}
	public DebugHandler(String message, int time) {
		this.message = message;
		this.time = time;
	}
	public void send() {
		System.out.println(message + "\nBuild time: " + time);
	}
}
