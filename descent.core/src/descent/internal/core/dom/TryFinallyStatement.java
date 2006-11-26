package descent.internal.core.dom;

import descent.core.dom.ICatch;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IStatement;
import descent.core.dom.ITryStatement;

public class TryFinallyStatement extends Statement implements ITryStatement {

	private final Statement s;
	private final Statement finalbody;

	public TryFinallyStatement(Loc loc, Statement s, Statement finalbody) {
		this.s = s;
		this.finalbody = finalbody;
	}
	
	public int getElementType() {
		return TRY_STATEMENT;
	}
	
	public ICatch[] getCatches() {
		if (s instanceof TryCatchStatement) {
			return ((TryCatchStatement) s).getCatches();
		} else {
			return new ICatch[0];
		}
	}

	public IStatement getFinally() {
		return finalbody;
	}

	public IStatement getTry() {
		if (s instanceof TryCatchStatement) {
			return ((TryCatchStatement) s).getTry();
		} else {
			return null;
		}
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, s);
			acceptChild(visitor, finalbody);
		}
		visitor.endVisit(this);
	}

}
