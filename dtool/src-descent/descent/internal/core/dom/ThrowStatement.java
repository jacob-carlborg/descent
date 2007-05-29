package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IStatement;
import descent.core.domX.IASTVisitor;

public class ThrowStatement extends Statement implements IStatement {

	public final Expression exp;

	public ThrowStatement(Expression exp) {
		this.exp = exp;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	public int getElementType() {
		return ElementTypes.THROW_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
