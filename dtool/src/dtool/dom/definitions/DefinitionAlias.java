package dtool.dom.definitions;


import util.tree.TreeVisitor;
import descent.internal.core.dom.AliasDeclaration;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntityConstrainedRef;
import dtool.model.IScope;

/**
 * A definition of an alias.
 */
public class DefinitionAlias extends Definition {
	
	public EntityConstrainedRef.NoConstraint target;
	
	public DefinitionAlias(AliasDeclaration elem) {
		convertDsymbol(elem);
		this.target = Entity.convertAnyEnt(elem.type);
	}
	
	public EArcheType getArcheType() {
		return EArcheType.Alias; // XXX: use efective archetype?
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, target);
			TreeVisitor.acceptChild(visitor, defname);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScope getScope() {
		return target.entity.getTargetDefUnit().getScope();
	}
}