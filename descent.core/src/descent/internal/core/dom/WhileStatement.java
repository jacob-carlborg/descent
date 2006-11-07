package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.dom.IWhileStatement;

public class WhileStatement extends Statement implements IWhileStatement {
	
	public Expression expr;
	public Statement body;

	public WhileStatement(Loc loc, Expression expr, Statement body) {
		this.expr = expr;
		this.body = body;
	}
	
	public IExpression getCondition() {
		return expr;
	}
	
	public IStatement getBody() {
		return body;
	}
	
	public int getStatementType() {
		return STATEMENT_WHILE;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, expr);
			acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
