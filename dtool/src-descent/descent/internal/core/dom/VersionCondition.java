package descent.internal.core.dom;

public class VersionCondition extends Condition {
	
	public Identifier id;

	public VersionCondition(Module mod, long level, Identifier id) {
		this.id = id;
	}
	
	@Override
	public int getConditionType() {
		return VERSION;
	}

}
