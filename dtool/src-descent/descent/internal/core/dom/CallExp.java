package descent.internal.core.dom;

import java.util.List;

import util.tree.TreeVisitor;

import descent.core.dom.IExpression;
import descent.core.domX.IASTVisitor;

public class CallExp extends Expression {
	
	Expression e;
	Expression[] args;

	public CallExp(Expression e, List<Expression> arguments) {
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
		return ElementTypes.CALL_EXPRESSION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, e);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}

}
