package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.IReturnStatement;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class ReturnStatement extends Statement implements IReturnStatement {
	
	public Expression exp;

	public ReturnStatement(Expression exp) {
		this.exp = exp;
	}

	public IExpression getReturnValue() {
		return exp;
	}
	
	public int getElementType() {
		return ElementTypes.RETURN_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
