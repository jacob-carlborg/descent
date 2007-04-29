package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IStatement;
import descent.core.domX.IASTVisitor;

public class DefaultStatement extends Statement {

	private final Statement s;

	public DefaultStatement(Statement s) {
		this.s = s;
	}
	
	public int getElementType() {
		return ElementTypes.DEFAULT_STATEMENT;
	}
	
	public IStatement getStatement() {
		return s;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
