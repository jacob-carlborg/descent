package descent.internal.core.dom;

import descent.core.dom.IProblem;

public class Problem implements IProblem {
	
	private String message;
	private int severity;
	private int id;
	private int offset;
	private int length;
	
	public Problem(String message, int severity, int id, int offset, int length) {
		this.message = message;
		this.severity = severity;
		this.id = id;
		this.offset = offset;
		this.length = length;
	}
	
	public int getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}
	
	public int getLength() {
		return length;
	}

	public int getOffset() {
		return offset;
	}

	public int getSeverity() {
		return severity;
	}
	
	public String toString() {
		return "P" + severity + " ["+length+"+"+offset+"] "+ message;
	}

}
