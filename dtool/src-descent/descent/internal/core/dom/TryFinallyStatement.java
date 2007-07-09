package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IDescentStatement;
import descent.core.dom.ITryStatement;
import descent.core.domX.IASTVisitor;

public class TryFinallyStatement extends Statement implements ITryStatement {

	public final Statement s;
	public final Statement finalbody;

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

	public IDescentStatement getFinally() {
		return finalbody;
	}

	public IDescentStatement getTry() {
		if (s instanceof TryCatchStatement) {
			return ((TryCatchStatement) s).getTry();
		} else {
			return null;
		}
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, s);
			TreeVisitor.acceptChild(visitor, finalbody);
		}
		visitor.endVisit(this);
	}

}
