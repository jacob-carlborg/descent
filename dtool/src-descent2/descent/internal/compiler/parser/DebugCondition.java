package descent.internal.compiler.parser;


public class DebugCondition extends DVCondition {
	
	public DebugCondition(long level, Identifier id) {
		super(level, id);
	}
	
	@Override
	public int getConditionType() {
		return DEBUG;
	}

}
