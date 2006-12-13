package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class ExpStatement extends Statement {
	
	private Expression exp;

	public ExpStatement(Expression exp) {
		this.exp = exp;
	}
	
	public int getElementType() {
		return ElementTypes.EXPRESSION_STATEMENT;
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
