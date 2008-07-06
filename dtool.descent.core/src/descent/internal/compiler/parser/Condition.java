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

	public boolean include(Scope sc, ScopeDsymbol s, SemanticContext context) {
		// TODO make it abstract and implement
		return true;
	}

	public abstract void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context);

	@Override
	public int getNodeType() {
		return getConditionType();
	}

	/*@Override
	protected void accept0(IASTVisitor visitor) {
		// TODO AST CONVERT
	}*/
	
}
