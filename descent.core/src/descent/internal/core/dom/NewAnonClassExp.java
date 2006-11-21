package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IBaseClass;
import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.INewAnonymousClassExpression;

public class NewAnonClassExp extends Expression implements INewAnonymousClassExpression {

	private ClassDeclaration cd;
	private IExpression[] newargs;
	private IExpression[] arguments;

	public NewAnonClassExp(Loc loc, Expression thisexp, List<Expression> newargs, ClassDeclaration cd, List<Expression> arguments) {
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
	
	public int getExpressionType() {
		return EXPRESSION_NEW_ANONYMOUS_CLASS;
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
	
	public IDElement[] getDeclarationDefinitions() {
		return cd.getDeclarationDefinitions();
	}
	
	public void accept(IDElementVisitor visitor) {
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
