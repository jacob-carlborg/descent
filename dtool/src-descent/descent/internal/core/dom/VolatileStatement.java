package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IDescentStatement;
import descent.core.dom.IVolatileStatement;
import descent.core.domX.IASTVisitor;

public class VolatileStatement extends Statement implements IVolatileStatement {

	public final Statement s;

	public VolatileStatement(Statement s) {
		this.s = s;
	}
	
	public IDescentStatement getStatement() {
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
