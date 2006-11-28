package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ICatchClause;
import descent.core.dom.ElementVisitor;
import descent.core.dom.IStatement;
import descent.core.dom.ITryStatement;

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
	
	public ICatchClause[] getCatches() {
		return catches.toArray(new ICatchClause[catches.size()]);
	}
	
	public IStatement getFinally() {
		return null;
	}
	
	public int getElementType() {
		return TRY_STATEMENT;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, body);
			acceptChildren(visitor, catches);
		}
		visitor.endVisit(this);
	}

}
