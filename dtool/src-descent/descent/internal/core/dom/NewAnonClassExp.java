package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDeclaration;
import descent.core.dom.INewAnonymousClassExpression;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class NewAnonClassExp extends Expression implements INewAnonymousClassExpression {

	private ClassDeclaration cd;
	private Expression[] newargs;
	private Expression[] arguments;

	public NewAnonClassExp(Expression thisexp, List<Expression> newargs, ClassDeclaration cd, List<Expression> arguments) {
		if (newargs == null) {
			this.newargs = new Expression[0];
		} else {
			this.newargs = newargs.toArray(new Expression[newargs.size()]);
		}
		if (arguments == null) {
			this.arguments = new Expression[0];
		} else {
			this.arguments = arguments.toArray(new Expression[arguments.size()]);
		}
		this.cd = cd;
	}
	
	public int getElementType() {
		return ElementTypes.NEW_ANONYMOUS_CLASS_EXPRESSION;
	}
	
	public Expression[] getCallArguments() {
		return newargs;
	}
	
	public Expression[] getConstructorArguments() {
		return arguments;
	}
	
	public BaseClass[] getBaseClasses() {
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
			acceptChildren(visitor, (AbstractElement[]) cd.getDeclarationDefinitions());
		}
		visitor.visit(this);
	}

}
