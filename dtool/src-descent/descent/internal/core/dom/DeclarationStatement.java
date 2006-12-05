package descent.internal.core.dom;

import descent.core.dom.IDeclarationStatement;
import descent.core.domX.ASTVisitor;

public class DeclarationStatement extends Statement implements IDeclarationStatement {

	private final Dsymbol d;

	public DeclarationStatement(Dsymbol d) {
		this.d = d;
	}
	
	public int getElementType() {
		return DECLARATION_STATEMENT;
	}
	
	public Declaration getDeclaration() {
		return (Declaration) d;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, d);
		}
		visitor.endVisit(this);
	}

}
