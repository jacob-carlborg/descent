package descent.internal.core.dom;

import java.util.List;

import util.tree.TreeVisitor;

import descent.core.dom.IStatement;
import descent.core.dom.ITryStatement;
import descent.core.domX.IASTVisitor;

public class TryCatchStatement extends Statement implements ITryStatement {

	private final Statement body;
	private final List<Catch> catches;

	public TryCatchStatement(Statement body, List<Catch> catches) {
		this.body = body;
		this.catches = catches;
	}
	
	public IStatement getTry() {
		return body;
	}
	
	public Catch[] getCatches() {
		return catches.toArray(new Catch[catches.size()]);
	}
	
	public IStatement getFinally() {
		return null;
	}
	
	public int getElementType() {
		return ElementTypes.TRY_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, body);
			TreeVisitor.acceptChildren(visitor, catches);
		}
		visitor.endVisit(this);
	}

}
