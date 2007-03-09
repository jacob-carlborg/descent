package dtool.dom.base;


import util.tree.TreeVisitor;
import dtool.dom.ast.ASTNeoVisitor;
import dtool.model.IScope;

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
			TreeVisitor.acceptChild(visitor, target);
			TreeVisitor.acceptChild(visitor, symbol);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScope getScope() {
		return target.getTargetDefUnit().getScope();
	}
}