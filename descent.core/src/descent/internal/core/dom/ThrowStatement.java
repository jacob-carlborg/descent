package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IThrowStatement;

public class ThrowStatement extends Statement implements IThrowStatement {

	private final Expression exp;

	public ThrowStatement(Expression exp) {
		this.exp = exp;
	}
	
	public IExpression getExpression() {
		return exp;
	}
	
	public int getElementType() {
		return THROW_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
