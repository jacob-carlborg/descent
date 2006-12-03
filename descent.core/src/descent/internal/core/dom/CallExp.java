package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ICallExpression;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;

public class CallExp extends Expression implements ICallExpression {
	
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
	
	public int getNodeType0() {
		return CALL_EXPRESSION;
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
