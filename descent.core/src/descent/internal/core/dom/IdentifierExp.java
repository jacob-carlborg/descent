package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IIdentifierExpression;
import descent.core.dom.ISimpleName;

public class IdentifierExp extends Expression implements IIdentifierExpression {
	
	public Identifier id;

	public IdentifierExp(Identifier id) {
		this.id = id;
		this.startPosition = id.startPosition;
		this.length = id.length;
	}
	
	public int getElementType() {
		return IDENTIFIER_EXPRESSION;
	}
	
	public ISimpleName getIdentifier() {
		return id;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, id);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		return id.string;
	}

}
