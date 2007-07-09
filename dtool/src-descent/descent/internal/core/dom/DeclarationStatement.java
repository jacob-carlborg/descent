package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class DeclarationStatement extends Statement {

	public final Dsymbol d;

	public DeclarationStatement(Dsymbol d) {
		this.d = d;
	}
	
	public int getElementType() {
		return ElementTypes.DECLARATION_STATEMENT;
	}
	
	public Declaration getDeclaration() {
		return (Declaration) d;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, d);
		}
		visitor.endVisit(this);
	}

}
