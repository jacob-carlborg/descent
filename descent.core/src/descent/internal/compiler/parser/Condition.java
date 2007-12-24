package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.ASTRangeLessNode;

// DMD 1.020
public abstract class Condition extends ASTRangeLessNode {

	public final static int DEBUG = 1;
	public final static int IFTYPE = 2;
	public final static int STATIC_IF = 3;
	public final static int VERSION = 4;

	public Loc loc;
	public int inc;

	public Condition(Loc loc) {
		this.loc = loc;
	}

	public abstract int getConditionType();
	
	@Override
	public int getLineNumber() {
		return loc.linnum;
	}
	
	public void setLineNumber(int lineNumber) {
		this.loc.linnum = lineNumber;
	}

	@Override
	public int getNodeType() {
		return getConditionType();
	}

	public abstract boolean include(Scope sc, IScopeDsymbol s, SemanticContext context);

	public abstract Condition syntaxCopy(SemanticContext context);

	public abstract void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context);

}
