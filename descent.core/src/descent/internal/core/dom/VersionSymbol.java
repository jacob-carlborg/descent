package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ISimpleName;
import descent.core.dom.IConditionAssignment;

public class VersionSymbol extends Dsymbol implements IConditionAssignment {

	public Identifier ident;

	public VersionSymbol(Identifier ident) {
		this.ident = ident;
	}

	public ISimpleName getValue() {
		return ident;
	}
	
	public int getNodeType0() {
		return CONDITION_ASSIGNMENT;
	}
	
	public int getConditionAssignmentType() {
		return CONDITION_VERSION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
