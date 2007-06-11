package dtool.dom.base;

import util.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.model.EntityResolver;
import dtool.model.IEntQualified;
import dtool.model.IScope;
import dtool.model.IScopeBinding;

public class EntQualified extends Entity implements IEntQualified {
	
	public Entity rootent;
	public EntitySingle subent;

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rootent);
			TreeVisitor.acceptChildren(visitor, subent);
		}
		visitor.endVisit(this);
	}
	
	@Override
	protected DefUnit getTargetDefUnitAsRoot() {
		IScope scope = rootent.getTargetScope();
		return EntityResolver.getDefUnitFromScope(scope, subent.name);
	}
	
	public String toString() {
		return rootent + "." + subent;
	}

	public IScopeBinding getRoot() {
		return rootent;
	}

	public EntitySingle getSubEnt() {
		return subent;
	}

}