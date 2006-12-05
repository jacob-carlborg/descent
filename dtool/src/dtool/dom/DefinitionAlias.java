package dtool.dom;

import dtool.dom.EntityReference.AnyEntityReference;
import dtool.dom.ext.ASTNeoVisitor;

/**
 * A definition of an alias.
 */
public class DefinitionAlias extends Definition {
	
	public AnyEntityReference target;

	
	public ArcheType getArcheType() {
		return ArcheType.Alias; // XXX: use efective archetype?
	}

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, target);
		}
		visitor.endVisit(this);
	}
}