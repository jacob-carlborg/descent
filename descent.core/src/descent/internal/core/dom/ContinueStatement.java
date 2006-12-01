package descent.internal.core.dom;

import descent.core.dom.IContinueStatement;
import descent.core.dom.ASTVisitor;
import descent.core.dom.ISimpleName;

public class ContinueStatement extends Statement implements IContinueStatement {
	
	public Identifier id;

	public ContinueStatement(Identifier ident) {
		this.id = ident;
	}
	
	public ISimpleName getLabel() {
		return id;
	}
	
	public int getElementType() {
		return CONTINUE_STATEMENT;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, id);
		}
		visitor.endVisit(this);
	}

}
