package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IExpressionStatement;

public class ExpStatement extends Statement implements IExpressionStatement {
	
	private Expression exp;

	public ExpStatement(Loc loc, Expression exp) {
		this.exp = exp;
	}
	
	public int getStatementType() {
		return STATEMENT_EXPRESSION;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
