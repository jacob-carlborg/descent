package descent.internal.compiler.parser;

public abstract class DVCondition extends Condition {
	
	public char[] ident;
	public long level;
	public int startPosition;
	public int length;
	
	public DVCondition(long level, char[] id) {
		this.level = level;
		this.ident = id;
	}

}
