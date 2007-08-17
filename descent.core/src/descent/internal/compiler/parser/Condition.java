package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.ASTRangeLessNode;

public abstract class Condition extends ASTRangeLessNode {
	
	public final static int DEBUG = 1;
	public final static int IFTYPE = 2;
	public final static int STATIC_IF = 3;
	public final static int VERSION = 4;
	public boolean inc;
	
	public abstract int getConditionType();

	public Condition syntaxCopy() {
		// TODO semantic
		return null;
	}

	public Object include(Object object, Object object2) {
		// TODO semantic
		return null;
	}

	public void toCBuffer(OutBuffer buf, HdrGenState hgs) {
		// TODO semantic
	}

	@Override
	public int getNodeType() {
		return getConditionType();
	}

	/*@Override
	protected void accept0(IASTVisitor visitor) {
		// TODO AST CONVERT
	}*/
	
}
