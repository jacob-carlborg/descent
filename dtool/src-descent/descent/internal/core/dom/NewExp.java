package descent.internal.core.dom;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;


import descent.core.dom.IExpression;
import descent.core.dom.IType;
import descent.core.domX.IASTVisitor;

public class NewExp extends Expression {
	
	public Expression[] arguments;
	public Type type;

	public NewExp(Expression thisexp, List<Expression> newargs, Type t, List<Expression> arguments) {
		this.type = t;
		this.arguments = arguments == null ? new Expression[0] : arguments.toArray(new Expression[arguments.size()]);
	}
	
	public IType getType() {
		return type;
	}
	
	public IExpression[] getArguments() {
		return arguments;
	}
	
	public int getElementType() {
		return ElementTypes.NEW_EXPRESSION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, type);
			TreeVisitor.acceptChildren(visitor, arguments);
		}
		visitor.endVisit(this);
	}

}
