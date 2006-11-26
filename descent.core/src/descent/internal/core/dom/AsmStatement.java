package descent.internal.core.dom;

import descent.core.dom.IAsmStatement;
import descent.core.dom.ElementVisitor;

public class AsmStatement extends Statement implements IAsmStatement {

	public AsmStatement(Loc loc, Token toklist) {
		// TODO Auto-generated constructor stub
	}
	
	public void accept0(ElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public int getElementType() {
		return ASM_STATEMENT;
	}

}
