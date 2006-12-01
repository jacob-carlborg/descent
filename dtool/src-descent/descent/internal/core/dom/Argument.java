package descent.internal.core.dom;

import descent.core.dom.IArgument;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class Argument extends AbstractElement implements IArgument {
	
	public Identifier id;
	public Type type;
	public InOut inout;
	public Expression defaultValue;

	public Argument(InOut inout, Type type, Identifier id, Expression defaultValue) {
		this.id = id;
		this.type = type;
		this.inout = inout;
		this.defaultValue = defaultValue;
	}
	
	public IName getName() {
		return id;
	}
	
	public IType getType() {
		return type;
	}
	
	public IExpression getDefaultValue() {
		return defaultValue;
	}
	
	public int getKind() {
		switch(inout) {
		case In: return IArgument.IN;
		case Out: return IArgument.OUT;
		case InOut: return IArgument.INOUT;
		default /* case Lazy */: return IArgument.LAZY;
		} 
	}

	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
			acceptChild(visitor, id);
			acceptChild(visitor, defaultValue);
		}
		visitor.endVisit(this);
	}

	public int getElementType() {
		return ARGUMENT;
	}

}
