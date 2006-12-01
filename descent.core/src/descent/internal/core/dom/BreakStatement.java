package descent.internal.core.dom;

import descent.core.dom.IBreakStatement;
import descent.core.dom.ASTVisitor;
import descent.core.dom.ISimpleName;

public class BreakStatement extends Statement implements IBreakStatement {
	
	public Identifier id;

	public BreakStatement(Identifier ident) {
		this.id = ident;
	}
	
	public ISimpleName getLabel() {
		return id;
	}
	
	public int getElementType() {
		return BREAK_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, id);
		}
		visitor.endVisit(this);
	}

}
