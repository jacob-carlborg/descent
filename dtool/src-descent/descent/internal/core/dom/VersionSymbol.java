package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IConditionAssignment;
import descent.core.dom.IName;
import descent.core.domX.ASTVisitor;

public class VersionSymbol extends Dsymbol implements IConditionAssignment {

	public Identifier ident;

	public VersionSymbol(Identifier ident) {
		this.ident = ident;
	}

	public IName getValue() {
		return ident;
	}
	
	public int getElementType() {
		return ElementTypes.CONDITION_ASSIGNMENT;
	}
	
	public int getConditionAssignmentType() {
		return CONDITION_VERSION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
