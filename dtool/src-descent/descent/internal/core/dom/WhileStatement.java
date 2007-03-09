package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.dom.IWhileStatement;
import descent.core.domX.ASTVisitor;

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
		return ElementTypes.WHILE_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, expr);
			TreeVisitor.acceptChild(visitor, body);
		}
		visitor.endVisit(this);
	}

}
