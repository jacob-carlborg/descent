package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.dom.IWithStatement;

public class WithStatement extends Statement implements IWithStatement {

	public Expression exp;
	public Statement body;

	public WithStatement(Loc loc, Expression exp, Statement body) {
		this.exp = exp;
		this.body = body;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	public IStatement getStatement() {
		return body;
	}
	
	public int getStatementType() {
		return STATEMENT_WITH;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
			acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
