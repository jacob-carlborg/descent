package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.IDeclarationStatement;

public class DeclarationStatement extends Statement implements IDeclarationStatement {

	private final Dsymbol d;

	public DeclarationStatement(Loc loc, Dsymbol d) {
		this.d = d;
	}
	
	public int getElementType() {
		return DECLARATION_STATEMENT;
	}
	
	public IDeclaration getDeclaration() {
		return (IDeclaration) d;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, d);
		}
		visitor.endVisit(this);
	}

}
