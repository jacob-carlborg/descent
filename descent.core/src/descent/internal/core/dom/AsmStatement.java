package descent.internal.core.dom;

import descent.core.dom.IAsmStatement;
import descent.core.dom.IDElementVisitor;

public class AsmStatement extends Statement implements IAsmStatement {

	public AsmStatement(Loc loc, Token toklist) {
		// TODO Auto-generated constructor stub
	}
	
	public void accept(IDElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public int getStatementType() {
		return STATEMENT_ASM;
	}

}
