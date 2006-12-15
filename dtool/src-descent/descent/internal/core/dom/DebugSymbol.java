package descent.internal.core.dom;

import descent.core.dom.IConditionAssignment;
import descent.core.dom.IName;
import descent.core.domX.ASTVisitor;

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
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
