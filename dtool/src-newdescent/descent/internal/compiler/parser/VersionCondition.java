package descent.internal.compiler.parser;

import descent.core.domX.IASTVisitor;

public class VersionCondition extends DVCondition {
	
	public VersionCondition(long level, Identifier id) {
		super(level, id);
	}
	
	@Override
	public int getConditionType() {
		return VERSION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

}
