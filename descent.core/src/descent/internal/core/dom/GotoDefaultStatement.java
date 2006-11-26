package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;

public class GotoDefaultStatement extends Statement {

	public GotoDefaultStatement(Loc loc) {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return GOTO_DEFAULT_STATEMENT;
	}
	
	public void accept(IDElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
