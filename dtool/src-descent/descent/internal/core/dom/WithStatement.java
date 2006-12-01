package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.dom.IWithStatement;
import descent.core.domX.ASTVisitor;

public class WithStatement extends Statement implements IWithStatement {

	public Expression exp;
	public Statement body;

	public WithStatement(Expression exp, Statement body) {
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
		return WITH_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
			acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
