package descent.internal.core.dom;

import descent.core.dom.IGotoDefaultStatement;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class GotoDefaultStatement extends Statement implements IGotoDefaultStatement {

	public GotoDefaultStatement() {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return ElementTypes.GOTO_DEFAULT_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
