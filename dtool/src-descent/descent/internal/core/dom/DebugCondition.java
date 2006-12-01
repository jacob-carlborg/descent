package descent.internal.core.dom;

public class DebugCondition extends Condition {
	
	public Identifier id;

	public DebugCondition(Module mod, long level, Identifier id) {
		this.id = id;
	}
	
	@Override
	public int getConditionType() {
		return DEBUG;
	}

}
