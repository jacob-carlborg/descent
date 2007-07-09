package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.dom.ITypeDotIdentifierExpression;
import descent.core.domX.IASTVisitor;

public class TypeDotIdExp extends Expression implements ITypeDotIdentifierExpression {
	
	public final Type t;
	public final Identifier ident;

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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, t);
			TreeVisitor.acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
