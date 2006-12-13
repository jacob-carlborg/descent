package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IArrayExpression;
import descent.core.dom.IExpression;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class ArrayExp extends Expression implements IArrayExpression {

	Expression e;
	Expression[] args;

	public ArrayExp(Expression e, List<Expression> arguments) {
		this.e = e;
		this.args = arguments.toArray(new Expression[arguments.size()]);
	}

	public IExpression[] getArguments() {
		return args;
	}

	public IExpression getExpression() {
		return e;
	}
	
	public int getElementType() {
		return ElementTypes.ARRAY_EXPRESSION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
			acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}

}
