package descent.core.dom;

import descent.internal.compiler.parser.Identifier;


public class DebugCondition extends Condition {
	
	public Identifier id;

	public DebugCondition(long level, Identifier id) {
		this.id = id;
	}
	
	@Override
	public int getConditionType() {
		return DEBUG;
	}

}
