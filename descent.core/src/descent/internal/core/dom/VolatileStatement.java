package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
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
	
	public int getElementType() {
		return VOLATILE_STATEMENT;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, s);
		}
		visitor.endVisit(this);
	}

}
