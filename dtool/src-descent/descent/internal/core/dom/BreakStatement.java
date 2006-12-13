package descent.internal.core.dom;

import descent.core.dom.IBreakStatement;
import descent.core.dom.IName;
import descent.core.dom.IElement.ElementTypes;
import descent.core.domX.ASTVisitor;

public class BreakStatement extends Statement implements IBreakStatement {
	
	public Identifier id;

	public BreakStatement(Identifier ident) {
		this.id = ident;
	}
	
	public IName getLabel() {
		return id;
	}
	
	public int getElementType() {
		return ElementTypes.BREAK_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, id);
		}
		visitor.endVisit(this);
	}

}
