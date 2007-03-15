package descent.core.dom;

import descent.internal.compiler.parser.Identifier;

public class VersionCondition extends Condition {
	
	public Identifier id;

	public VersionCondition(long level, Identifier id) {
		this.id = id;
	}
	
	@Override
	public int getConditionType() {
		return VERSION;
	}

}