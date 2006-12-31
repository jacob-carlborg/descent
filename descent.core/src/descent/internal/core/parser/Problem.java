package descent.internal.core.parser;

import descent.core.compiler.IProblem;

public class Problem implements IProblem {
	
	private String message;
	private boolean isError;
	private int categoryId;
	private int id;
	private int sourceStart;
	private int sourceEnd;
	private int sourceLineNumber;
	
	private Problem() { }
	
	public static Problem newSyntaxError(String message, int id, int line, int sourceStart, int length) {
		Problem p = new Problem();
		p.message = message;
		p.isError = true;
		p.id = id;
		p.categoryId = CAT_SYNTAX;
		p.sourceLineNumber = line;
		p.sourceStart = sourceStart;
		p.sourceEnd = sourceStart + length;
		return p;
	}
	
	public int getID() {
		return id;
	}

	public String getMessage() {
		return message;
	}
	
	public int getLength() {
		return sourceEnd;
	}

	public int getSourceStart() {
		return sourceStart;
	}
	
	public int getSourceEnd() {
		return sourceEnd;
	}
	
	public boolean isError() {
		return isError;
	}
	
	public boolean isWarning() {
		return !isError;
	}
	
	public int getSourceLineNumber() {
		return sourceLineNumber;
	}
	
	public int getCategoryID() {
		return categoryId;
	}
	
	public String getMarkerType() {
		return "descent.core.problem";
	}
	
	public String[] getArguments() {
		return new String[0];
	}
	
	public char[] getOriginatingFileName() {
		return new char[0];
	}

	public void setSourceEnd(int sourceEnd) {
		this.sourceEnd = sourceEnd;
	}

	public void setSourceLineNumber(int lineNumber) {
		this.sourceLineNumber = lineNumber;
	}

	public void setSourceStart(int sourceStart) {
		this.sourceStart = sourceStart;
	}

}
