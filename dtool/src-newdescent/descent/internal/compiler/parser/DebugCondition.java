package descent.internal.compiler.parser;

import descent.core.domX.IASTVisitor;


public class DebugCondition extends DVCondition {
	
	public DebugCondition(long level, Identifier id) {
		super(level, id);
	}
	
	@Override
	public int getConditionType() {
		return DEBUG;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

}
