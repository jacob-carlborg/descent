package descent.internal.compiler.parser;

public abstract class DVCondition extends Condition {
	
	public Identifier ident;
	public long level;
	
	public DVCondition(long level, Identifier id) {
		this.level = level;
		this.ident = id;
	}

}
