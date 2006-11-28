package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.dom.ISynchronizedStatement;

public class SynchronizedStatement extends Statement implements ISynchronizedStatement {

	private final Expression exp;
	private final Statement body;

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
		return SYNCHRONIZED_STATEMENT;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
			acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
