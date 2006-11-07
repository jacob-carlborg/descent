package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IArrayExpression;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;

public class ArrayExp extends Expression implements IArrayExpression {

	Expression e;
	Expression[] args;

	public ArrayExp(Loc loc, Expression e, List<Expression> arguments) {
		this.e = e;
		this.args = arguments.toArray(new Expression[arguments.size()]);
	}

	public IExpression[] getArguments() {
		return args;
	}

	public IExpression getExpression() {
		return e;
	}
	
	public int getExpressionType() {
		return EXPRESSION_ARRAY;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, e);
			acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}

}
