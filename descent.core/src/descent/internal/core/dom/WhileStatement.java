package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.dom.IWhileStatement;

public class WhileStatement extends Statement implements IWhileStatement {
	
	public Expression expr;
	public Statement body;

	public WhileStatement(Expression expr, Statement body) {
		this.expr = expr;
		this.body = body;
	}
	
	public IExpression getCondition() {
		return expr;
	}
	
	public IStatement getBody() {
		return body;
	}
	
	public int getElementType() {
		return WHILE_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, expr);
			acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
