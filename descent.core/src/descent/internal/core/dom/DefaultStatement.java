package descent.internal.core.dom;

import descent.core.dom.IDefaultStatement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IStatement;

public class DefaultStatement extends Statement implements IDefaultStatement {

	private final Statement s;

	public DefaultStatement(Loc loc, Statement s) {
		this.s = s;
	}
	
	public int getStatementType() {
		return STATEMENT_DEFAULT;
	}
	
	public IStatement getStatement() {
		return s;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
