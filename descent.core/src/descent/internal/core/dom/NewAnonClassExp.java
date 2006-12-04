package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IBaseClass;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.IExpression;
import descent.core.dom.INewAnonymousClassExpression;

public class NewAnonClassExp extends Expression implements INewAnonymousClassExpression {

	private AggregateDeclaration ad;
	private IExpression[] newargs;
	private IExpression[] arguments;

	public NewAnonClassExp(Expression thisexp, List<Expression> newargs, AggregateDeclaration cd, List<Expression> arguments) {
		if (newargs == null) {
			this.newargs = new IExpression[0];
		} else {
			this.newargs = newargs.toArray(new IExpression[newargs.size()]);
		}
		if (arguments == null) {
			this.arguments = new IExpression[0];
		} else {
			this.arguments = arguments.toArray(new IExpression[arguments.size()]);
		}
		this.ad = cd;
	}
	
	public int getNodeType0() {
		return NEW_ANONYMOUS_CLASS_EXPRESSION;
	}
	
	public IExpression[] getCallArguments() {
		return newargs;
	}
	
	public IExpression[] getConstructorArguments() {
		return arguments;
	}
	
	public IBaseClass[] getBaseClasses() {
		return ad.baseClasses().toArray(new IBaseClass[ad.baseClasses().size()]);
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		return ad.declarations().toArray(new IDeclaration[ad.declarations().size()]);
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, newargs);
			acceptChildren(visitor, arguments);
			acceptChildren(visitor, ad.baseClasses());
			acceptChildren(visitor, ad.declarations());
		}
		visitor.visit(this);
	}

}
