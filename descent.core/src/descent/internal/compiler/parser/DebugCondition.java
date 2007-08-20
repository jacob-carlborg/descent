package descent.internal.compiler.parser;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ast.IASTVisitor;


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
	
	@Override
	public char[] toCharArray() {
		if (id != null) {
			return id.string;
		} else {
			return String.valueOf(level).toCharArray();
		}
	}

}
