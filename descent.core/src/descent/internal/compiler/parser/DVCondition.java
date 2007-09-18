package descent.internal.compiler.parser;

public abstract class DVCondition extends Condition {
	
	public char[] ident;
	public long level;
	public int startPosition;
	public int length;
	
	public DVCondition(Loc loc, long level, char[] id) {
		super(loc);
		this.level = level;
		this.ident = id;
	}

}
