package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.IDeclarationStatement;

public class DeclarationStatement extends Statement implements IDeclarationStatement {

	private final Dsymbol d;

	public DeclarationStatement(Dsymbol d) {
		this.d = d;
	}
	
	public int getNodeType0() {
		return DECLARATION_STATEMENT;
	}
	
	public IDeclaration getDeclaration() {
		return (IDeclaration) d;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, d);
		}
		visitor.endVisit(this);
	}

}
