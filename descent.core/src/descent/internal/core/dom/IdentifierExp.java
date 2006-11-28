package descent.internal.core.dom;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IIdentifierExpression;
import descent.core.dom.IName;

public class IdentifierExp extends Expression implements IIdentifierExpression {
	
	public Identifier id;

	public IdentifierExp(Identifier id) {
		this.id = id;
		this.start = id.start;
		this.length = id.length;
	}
	
	public int getElementType() {
		return IDENTIFIER_EXPRESSION;
	}
	
	public IName getIdentifier() {
		return id;
	}
	
	public void accept0(ElementVisitor visitor) {
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
