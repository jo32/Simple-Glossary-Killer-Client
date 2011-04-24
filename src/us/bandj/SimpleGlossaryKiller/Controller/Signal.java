package us.bandj.SimpleGlossaryKiller.Controller;

public class Signal {
	private int code;
	private Object message;

	public Signal() {
	}

	public Signal(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}
	
}
