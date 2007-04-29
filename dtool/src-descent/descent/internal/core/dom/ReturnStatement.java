package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.domX.IASTVisitor;

public class ReturnStatement extends Statement implements IStatement {
	
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
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
