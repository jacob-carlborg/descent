package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.domX.IASTVisitor;

public class AddAssignExp extends BinaryExpression implements IExpression {
	
	public final boolean isUnary;

	public AddAssignExp(Expression e, Expression exp) {
		this(e, exp, false);
	}
	
	public AddAssignExp(Expression e, Expression exp, boolean isUnary) {
		super(e, exp);
		this.isUnary = isUnary;
	}
	
	public int getElementType() {
		return isUnary ? ElementTypes.UNARY_EXPRESSION : ElementTypes.BINARY_EXPRESSION;
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.ADD_ASSIGN;
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.PRE_INCREMENT;
	}
	
	public IExpression getInnerExpression() {
		return getLeftExpression();
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		if (isUnary) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChild(visitor, e1);
				TreeVisitor.acceptChild(visitor, e2);
			}
			visitor.endVisit((BinaryExpression) this);
		} else {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChild(visitor, e1);
				TreeVisitor.acceptChild(visitor, e2);
			}
			visitor.endVisit((BinaryExpression) this);
		}
	}

}
