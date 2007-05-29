package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.dom.ISynchronizedStatement;
import descent.core.domX.IASTVisitor;

public class SynchronizedStatement extends Statement implements ISynchronizedStatement {

	public final Expression exp;
	public final Statement body;

	public SynchronizedStatement(Expression exp, Statement body) {
		this.exp = exp;
		this.body = body;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	public IStatement getStatement() {
		return body;
	}
	
	public int getElementType() {
		return ElementTypes.SYNCHRONIZED_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
			TreeVisitor.acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
