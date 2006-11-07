package descent.internal.core.dom;

import descent.core.dom.IDeclarationStatement;
import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;

public class DeclarationStatement extends Statement implements IDeclarationStatement {

	private final Dsymbol d;

	public DeclarationStatement(Loc loc, Dsymbol d) {
		this.d = d;
	}
	
	public int getStatementType() {
		return STATEMENT_DECLARATION;
	}
	
	public IDElement getDeclaration() {
		return d;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, d);
		}
		visitor.endVisit(this);
	}

}
