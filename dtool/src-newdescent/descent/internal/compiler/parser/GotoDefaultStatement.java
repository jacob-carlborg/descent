package descent.internal.compiler.parser;

import descent.core.domX.IASTVisitor;

public class GotoDefaultStatement extends Statement {
	
	public GotoDefaultStatement(Loc loc) {
		super(loc);
	}

	@Override
	public int getNodeType() {
		return GOTO_DEFAULT_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

}
