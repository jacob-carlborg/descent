package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IReturnStatement;

public class ReturnStatement extends Statement implements IReturnStatement {
	
	public Expression exp;

	public ReturnStatement(Expression exp) {
		this.exp = exp;
	}

	public IExpression getReturnValue() {
		return exp;
	}
	
	public int getElementType() {
		return RETURN_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
