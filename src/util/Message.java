package util;

import java.io.Serializable;

public class Message implements Serializable{
	/**
	 * Auto-generated UID
	 */
	private static final long serialVersionUID = 695588852759178417L;

	private String action;

	private long elapsedTime;

	public Message(String action) {
		this.action = action;
	}
	
	public Message() {
		action = "";
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}
}
