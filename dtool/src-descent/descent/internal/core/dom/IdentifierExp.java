package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IIdentifierExpression;
import descent.core.dom.IName;
import descent.core.domX.IASTVisitor;

public class IdentifierExp extends Expression implements IIdentifierExpression {
	
	public Identifier id;

	public IdentifierExp(Identifier id) {
		this.id = id;
		this.startPos = id.startPos;
		this.length = id.length;
	}
	
	public int getElementType() {
		return ElementTypes.IDENTIFIER_EXPRESSION;
	}
	
	public IName getIdentifier() {
		return id;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, id);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		return id.string;
	}

}
