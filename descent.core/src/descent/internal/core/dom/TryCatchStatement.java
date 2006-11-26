package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ICatch;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IStatement;
import descent.core.dom.ITryStatement;

public class TryCatchStatement extends Statement implements ITryStatement {

	private final Statement body;
	private final List<Catch> catches;

	public TryCatchStatement(Loc loc, Statement body, List<Catch> catches) {
		this.body = body;
		this.catches = catches;
	}
	
	public IStatement getTry() {
		return body;
	}
	
	public ICatch[] getCatches() {
		return catches.toArray(new ICatch[catches.size()]);
	}
	
	public IStatement getFinally() {
		return null;
	}
	
	public int getElementType() {
		return TRY_STATEMENT;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, body);
			acceptChildren(visitor, catches);
		}
		visitor.endVisit(this);
	}

}
