package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;

public class GotoDefaultStatement extends Statement {

	public GotoDefaultStatement(Loc loc) {
		// TODO Auto-generated constructor stub
	}
	
	public int getStatementType() {
		return STATEMENT_GOTO_DEFAULT;
	}
	
	public void accept(IDElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
