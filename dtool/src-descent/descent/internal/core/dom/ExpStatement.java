package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.domX.IASTVisitor;

public class ExpStatement extends Statement {
	
	public Expression exp;

	public ExpStatement(Expression exp) {
		this.exp = exp;
	}
	
	public int getElementType() {
		return ElementTypes.EXPRESSION_STATEMENT;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
