package descent.internal.compiler.parser;

public abstract class DVCondition extends Condition {
	
	public Identifier id;
	
	public DVCondition(long level, Identifier id) {
		this.id = id;
	}

}
