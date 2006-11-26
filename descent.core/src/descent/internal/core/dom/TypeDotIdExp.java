package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.dom.ITypeDotIdentifierExpression;

public class TypeDotIdExp extends Expression implements ITypeDotIdentifierExpression {
	
	private final Type t;
	private final Identifier ident;

	public TypeDotIdExp(Loc loc, Type t, Identifier ident) {
		this.t = t;
		this.ident = ident;
		this.start = t.start;
		this.length = ident.start + ident.length - this.start;
	}

	public IName getProperty() {
		return ident;
	}

	public IType getType() {
		return t;
	}
	
	public int getElementType() {
		return TYPE_DOT_IDENTIFIER_EXPRESSION;
	}
	
	@Override
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, t);
			acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
