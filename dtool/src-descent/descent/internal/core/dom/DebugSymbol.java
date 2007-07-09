package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IConditionAssignment;
import descent.core.dom.IName;
import descent.core.domX.IASTVisitor;

public class DebugSymbol extends Dsymbol implements IConditionAssignment {

	public Identifier ident;

	public DebugSymbol(Identifier ident) {
		this.ident = ident;
	}

	public IName getValue() {
		return ident;
	}
	
	public int getElementType() {
		return ElementTypes.CONDITION_ASSIGNMENT;
	}
	
	public int getConditionAssignmentType() {
		return CONDITION_DEBUG;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
