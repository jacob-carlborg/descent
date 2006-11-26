package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IGotoDefaultStatement;

public class GotoDefaultStatement extends Statement implements IGotoDefaultStatement {

	public GotoDefaultStatement(Loc loc) {
		// TODO Auto-generated constructor stub
	}
	
	public int getElementType() {
		return GOTO_DEFAULT_STATEMENT;
	}
	
	public void accept0(ElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
