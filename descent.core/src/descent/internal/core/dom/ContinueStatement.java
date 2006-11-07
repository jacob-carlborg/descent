package descent.internal.core.dom;

import descent.core.dom.IContinueStatement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IName;

public class ContinueStatement extends Statement implements IContinueStatement {
	
	public Identifier id;

	public ContinueStatement(Loc loc, Identifier ident) {
		this.id = ident;
	}
	
	public IName getLabel() {
		return id;
	}
	
	public int getStatementType() {
		return STATEMENT_CONTINUE;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, id);
		}
		visitor.endVisit(this);
	}

}
