package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ISimpleName;
import descent.core.dom.IType;
import descent.core.dom.ITypeDotIdentifierExpression;

public class TypeDotIdExp extends Expression implements ITypeDotIdentifierExpression {
	
	private final DmdType t;
	private final Identifier ident;

	public TypeDotIdExp(DmdType t, Identifier ident) {
		this.t = t;
		this.ident = ident;
		this.startPosition = t.startPosition;
		this.length = ident.startPosition + ident.length - this.startPosition;
	}

	public ISimpleName getProperty() {
		return ident;
	}

	public IType getType() {
		return t;
	}
	
	public int getNodeType0() {
		return TYPE_DOT_IDENTIFIER_EXPRESSION;
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
