package descent.internal.core.dom;

import descent.core.dom.IGotoDefaultStatement;
import descent.core.domX.IASTVisitor;

public class GotoDefaultStatement extends Statement implements IGotoDefaultStatement {

	public GotoDefaultStatement() {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return ElementTypes.GOTO_DEFAULT_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
