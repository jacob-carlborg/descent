package descent.internal.compiler.parser;

public class VersionCondition extends DVCondition {
	
	public VersionCondition(long level, Identifier id) {
		super(level, id);
	}
	
	@Override
	public int getConditionType() {
		return VERSION;
	}

}
