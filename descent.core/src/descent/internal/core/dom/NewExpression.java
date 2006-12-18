package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.INewExpression;
import descent.core.dom.IType;

public class NewExpression extends Expression implements INewExpression {
	
	private IExpression[] arguments;
	private Type type;

	public NewExpression(AST ast, Expression thisexp, List<Expression> newargs, Type t, List<Expression> arguments) {
		super(ast);
		this.type = t;
		this.arguments = arguments == null ? new IExpression[0] : arguments.toArray(new IExpression[arguments.size()]);
	}
	
	public IType getType() {
		return type;
	}
	
	public IExpression[] getArguments() {
		return arguments;
	}
	
	public int getNodeType0() {
		return NEW_EXPRESSION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
			acceptChildren(visitor, arguments);
		}
		visitor.endVisit(this);
	}

}
