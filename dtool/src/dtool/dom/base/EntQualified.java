package dtool.dom.base;

import util.tree.TreeVisitor;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.model.EntityResolver;
import dtool.model.IDefUnitReference;
import dtool.model.IEntQualified;
import dtool.model.IScope;

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
	
	public DefUnit getTargetDefUnit() {
		DefUnit defunit = root.getTargetDefUnit();
		if(defunit == null)
			return null;
		IScope scope = defunit.getMembersScope();
		return EntityResolver.getDefUnitFromScope(scope, subent.name);
	}

}