package dtool.dom.references;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.EntitySearch;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IEntQualified;
import dtool.refmodel.IScopeNode;

public class EntQualified extends Entity implements IEntQualified {

	public IDefUnitReference root; //Entity or Expression
	public EntitySingle subent;

	public EntQualified() {
		super();
	}
	

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, root);
			TreeVisitor.acceptChildren(visitor, subent);
		}
		visitor.endVisit(this);
	}

	
	public String toString() {
		return root + "." + subent;
	}
	
	public ASTNode getRootExp() {
		return (ASTNode) this.root;
	}

	public IDefUnitReference getRoot() {
		return root;
	}

	public EntitySingle getSubEnt() {
		return subent;
	}
	
	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		Collection<DefUnit> defunits = root.findTargetDefUnits(false);
		if(defunits == null)
			return null;

		EntitySearch search = EntitySearch.newSearch(subent.name, this);
		
		for (DefUnit unit : defunits) {
			IScopeNode scope = unit.getMembersScope();
			EntityResolver.findDefUnitInScope(scope, search);
			if(search.isFinished())
				return search.getDefUnits();
		}
		return search.getDefUnits();
	}

}