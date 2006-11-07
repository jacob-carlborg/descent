package descent.internal.core.dom;

import descent.core.dom.IDoWhileStatement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;

public class DoStatement extends Statement implements IDoWhileStatement {
	
	public Expression expr;
	public Statement body;

	public DoStatement(Loc loc, Statement body, Expression expr) {
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
		return STATEMENT_DO_WHILE;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, body);
			acceptChild(visitor, expr);			
		}
		visitor.endVisit(this);
	}

}
