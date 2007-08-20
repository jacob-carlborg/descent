package descent.internal.compiler.parser;

public abstract class DVCondition extends Condition {
	
	public Identifier id;
	public long level;
	
	public DVCondition(long level, Identifier id) {
		this.level = level;
		this.id = id;
	}

}
