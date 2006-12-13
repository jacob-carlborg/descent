package descent.internal.core.dom;

import descent.core.dom.IStatement;
import descent.core.dom.ITryStatement;
import descent.core.domX.ASTVisitor;

public class TryFinallyStatement extends Statement implements ITryStatement {

	private final Statement s;
	private final Statement finalbody;

	public TryFinallyStatement(Statement s, Statement finalbody) {
		this.s = s;
		this.finalbody = finalbody;
	}
	
	public int getElementType() {
		return ElementTypes.TRY_STATEMENT;
	}
	
	public Catch[] getCatches() {
		if (s instanceof TryCatchStatement) {
			return ((TryCatchStatement) s).getCatches();
		} else {
			return new Catch[0];
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
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, s);
			acceptChild(visitor, finalbody);
		}
		visitor.endVisit(this);
	}

}
