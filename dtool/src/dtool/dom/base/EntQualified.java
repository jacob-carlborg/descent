package dtool.dom.base;

import util.tree.TreeVisitor;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.model.BindingResolver;
import dtool.model.IScope;

public class EntQualified extends Entity {
	
	public Entity topent;
	public EntitySingle baseent;

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, topent);
			TreeVisitor.acceptChildren(visitor, baseent);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public DefUnit getTargetDefUnit() {
		IScope scope = topent.getTargetDefUnit().getScope();
		EntitySingle id = baseent;
		BindingResolver.getDefUnit(scope, id.name);

		return null;
	}
	
	public String toString() {
		return topent + "." + baseent;
	}

}
