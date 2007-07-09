package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IContinueStatement;
import descent.core.dom.IName;
import descent.core.domX.IASTVisitor;

public class ContinueStatement extends Statement implements IContinueStatement {
	
	public Identifier id;

	public ContinueStatement(Identifier ident) {
		this.id = ident;
	}
	
	public IName getLabel() {
		return id;
	}
	
	public int getElementType() {
		return ElementTypes.CONTINUE_STATEMENT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, id);
		}
		visitor.endVisit(this);
	}

}
