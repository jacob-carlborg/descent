package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IStatement;
import descent.core.dom.IVolatileStatement;

public class VolatileStatement extends Statement implements IVolatileStatement {

	private final Statement s;

	public VolatileStatement(Loc loc, Statement s) {
		this.s = s;
	}
	
	public IStatement getStatement() {
		return s;
	}
	
	public int getStatementType() {
		return STATEMENT_VOLATILE;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
