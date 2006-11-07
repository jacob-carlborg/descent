package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IGotoStatement;
import descent.core.dom.IName;

public class GotoStatement extends Statement implements IGotoStatement {

	private final Identifier ident;

	public GotoStatement(Loc loc, Identifier ident) {
		this.ident = ident;
	}
	
	public int getStatementType() {
		return STATEMENT_GOTO;
	}
	
	public IName getLabel() {
		return ident;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
