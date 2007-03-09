package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.domX.ASTVisitor;

public class DoStatement extends Statement {
	
	public Expression expr;
	public Statement body;

	public DoStatement(Statement body, Expression expr) {
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
		return ElementTypes.DO_WHILE_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, body);
			TreeVisitor.acceptChild(visitor, expr);			
		}
		visitor.endVisit(this);
	}

}
