package dtool.dom;


import dtool.dombase.ASTNeoVisitor;
import dtool.dombase.IScope;

/**
 * A definition of an alias.
 */
public class DefinitionAlias extends Definition {
	
	public Entity target;

	
	public EArcheType getArcheType() {
		return EArcheType.Alias; // XXX: use efective archetype?
	}

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, target);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScope getScope() {
		return target.getReferencedDefUnit().getScope();
	}
}