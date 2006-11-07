package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IReturnStatement;

public class ReturnStatement extends Statement implements IReturnStatement {
	
	public Expression exp;

	public ReturnStatement(Loc loc, Expression exp) {
		this.exp = exp;
	}

	public IExpression getReturnValue() {
		return exp;
	}
	
	public int getStatementType() {
		return STATEMENT_RETURN;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
