package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

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
	
	@Override
	public char[] toCharArray() {
		if (id != null) {
			return id.string;
		} else {
			return String.valueOf(level).toCharArray();
		}
	}

}
