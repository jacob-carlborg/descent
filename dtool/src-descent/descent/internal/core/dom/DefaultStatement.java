package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IDescentStatement;
import descent.core.domX.IASTVisitor;

public class DefaultStatement extends Statement {

	public final Statement s;

	public DefaultStatement(Statement s) {
		this.s = s;
	}
	
	public int getElementType() {
		return ElementTypes.DEFAULT_STATEMENT;
	}
	
	public IDescentStatement getStatement() {
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
