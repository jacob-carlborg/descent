package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IStatement;
import descent.core.dom.ITryStatement;
import descent.core.domX.ASTVisitor;

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
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, body);
			acceptChildren(visitor, catches);
		}
		visitor.endVisit(this);
	}

}
