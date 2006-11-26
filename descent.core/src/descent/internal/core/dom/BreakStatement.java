package descent.internal.core.dom;

import descent.core.dom.IBreakStatement;
import descent.core.dom.ElementVisitor;
import descent.core.dom.IName;

public class BreakStatement extends Statement implements IBreakStatement {
	
	public Identifier id;

	public BreakStatement(Loc loc, Identifier ident) {
		this.id = ident;
	}
	
	public IName getLabel() {
		return id;
	}
	
	public int getElementType() {
		return BREAK_STATEMENT;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, id);
		}
		visitor.endVisit(this);
	}

}
