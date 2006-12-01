package descent.internal.core.dom;

import descent.core.dom.IScopeStatement;
import descent.core.dom.IStatement;
import descent.core.domX.ASTVisitor;

public class ScopeStatement extends Statement implements IScopeStatement {

	private final Statement s;

	public ScopeStatement(Statement s) {
		this.s = s;
	}
	
	public IStatement getStatement() {
		return s;
	}
	
	public int getElementType() {
		return SCOPE_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
