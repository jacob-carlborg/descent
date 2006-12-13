package descent.internal.core.dom;

import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.dom.ITypeDotIdentifierExpression;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class TypeDotIdExp extends Expression implements ITypeDotIdentifierExpression {
	
	private final Type t;
	private final Identifier ident;

	public TypeDotIdExp(Type t, Identifier ident) {
		this.t = t;
		this.ident = ident;
		this.startPos = t.startPos;
		this.length = ident.startPos + ident.length - this.startPos;
	}

	public IName getProperty() {
		return ident;
	}

	public IType getType() {
		return t;
	}
	
	public int getElementType() {
		return ElementTypes.TYPE_DOT_IDENTIFIER_EXPRESSION;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, t);
			acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
