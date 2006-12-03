package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IBaseClass;
import descent.core.dom.ASTVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.IExpression;
import descent.core.dom.INewAnonymousClassExpression;

public class NewAnonClassExp extends Expression implements INewAnonymousClassExpression {

	private ClassDeclaration cd;
	private IExpression[] newargs;
	private IExpression[] arguments;

	public NewAnonClassExp(Expression thisexp, List<Expression> newargs, ClassDeclaration cd, List<Expression> arguments) {
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
		this.cd = cd;
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
		return cd.getBaseClasses();
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		return cd.getDeclarationDefinitions();
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChildren(visitor, newargs);
			acceptChildren(visitor, arguments);
			acceptChildren(visitor, cd.getBaseClasses());
			acceptChildren(visitor, cd.getDeclarationDefinitions());
		}
		visitor.visit(this);
	}

}
