package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.domX.ASTVisitor;

public abstract class UnaryExpression extends Expression implements IExpression {
	
	public interface IUnaryExpression2 {
	
		int ADDRESS = 1;
		int PRE_INCREMENT = 2;
		int PRE_DECREMENT = 3;
		int POINTER = 4;
		int NEGATIVE = 5;
		int POSITIVE = 6;
		int NOT = 7;
		int INVERT = 8;
		int POST_INCREMENT = 9;
		int POST_DECREMENT = 10;
	}

	private final Expression exp;

	public UnaryExpression(Expression exp) {
		this.exp = exp;
	}
	
	public IExpression getInnerExpression() {
		return exp;
	}
	
	public int getElementType() {
		return ElementTypes.UNARY_EXPRESSION;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, exp);
		}
		visitor.endVisit(this);
	}

}
