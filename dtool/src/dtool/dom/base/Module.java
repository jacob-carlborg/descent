package dtool.dom.base;

import java.util.ArrayList;
import java.util.List;

import util.tree.TreeVisitor;

import dtool.dom.ast.ASTNeoVisitor;
import dtool.model.IScope;

/**
 * D Module
 */
public class Module extends DefUnit implements IScope {
	
	public DeclarationModule md;
	public ASTNode[] members; //FIXME
	

	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, md);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

	public static class DeclarationModule extends ASTNeoNode {

		public EntitySingle[] packages;
		public EntitySingle moduleName; // XXX: SymbolReference?

		public void accept0(ASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, packages);
				TreeVisitor.acceptChild(visitor, moduleName);
			}
			visitor.endVisit(this);
		}
	}
	
	
	public List<DefUnit> getDefUnits() {
		//TODO cache
		List<DefUnit> defunits = new ArrayList<DefUnit>();
		for(ASTNode elem: members) {
			if(elem instanceof DefUnit)
				defunits.add((DefUnit)elem);
		}
		return defunits;
	}

	@Override
	public IScope getScope() {
		return this;
	}

}
