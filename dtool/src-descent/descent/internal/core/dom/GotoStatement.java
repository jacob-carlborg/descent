package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IGotoStatement;
import descent.core.dom.IName;
import descent.core.domX.IASTVisitor;

public class GotoStatement extends Statement implements IGotoStatement {

	public final Identifier ident;

	public GotoStatement(Identifier ident) {
		this.ident = ident;
	}
	
	public int getElementType() {
		return ElementTypes.GOTO_STATEMENT;
	}
	
	public IName getLabel() {
		return ident;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
