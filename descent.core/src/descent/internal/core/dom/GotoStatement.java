package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IGotoStatement;
import descent.core.dom.ISimpleName;

public class GotoStatement extends Statement implements IGotoStatement {

	private final Identifier ident;

	public GotoStatement(Identifier ident) {
		this.ident = ident;
	}
	
	public int getElementType() {
		return GOTO_STATEMENT;
	}
	
	public ISimpleName getLabel() {
		return ident;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
