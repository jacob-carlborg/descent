package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IStatement;
import descent.core.dom.IVolatileStatement;
import descent.core.domX.IASTVisitor;

public class VolatileStatement extends Statement implements IVolatileStatement {

	public final Statement s;

	public VolatileStatement(Statement s) {
		this.s = s;
	}
	
	public IStatement getStatement() {
		return s;
	}
	
	public int getElementType() {
		return ElementTypes.VOLATILE_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
