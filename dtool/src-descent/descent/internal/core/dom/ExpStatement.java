package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.IExpressionStatement;
import descent.core.domX.ASTVisitor;

public class ExpStatement extends Statement implements IExpressionStatement {
	
	private Expression exp;

	public ExpStatement(Expression exp) {
		this.exp = exp;
	}
	
	public int getElementType() {
		return EXPRESSION_STATEMENT;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
