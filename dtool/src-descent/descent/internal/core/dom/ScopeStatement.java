package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IScopeStatement;
import descent.core.dom.IStatement;
import descent.core.domX.IASTVisitor;

public class ScopeStatement extends Statement implements IScopeStatement {

	public final Statement s;

	public ScopeStatement(Statement s) {
		this.s = s;
	}
	
	public IStatement getStatement() {
		return s;
	}
	
	public int getElementType() {
		return ElementTypes.SCOPE_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
