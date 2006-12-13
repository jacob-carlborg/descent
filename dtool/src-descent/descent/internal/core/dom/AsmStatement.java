package descent.internal.core.dom;

import descent.core.domX.ASTVisitor;

public class AsmStatement extends Statement {

	public AsmStatement(Token toklist) {
		// TODO Auto-generated constructor stub
	}
	
	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public int getElementType() {
		return ElementTypes.ASM_STATEMENT;
	}

}
