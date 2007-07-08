package dtool.dom.references;

import util.tree.TreeVisitor;
import descent.internal.core.dom.Identifier;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IEntQualified;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;

public class EntModuleQualified extends Entity implements IEntQualified {

	public EntitySingle subent;

	public EntModuleQualified(Identifier elem) {
		convertNode(elem);
		subent = EntitySingle.convert(elem);
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, subent);
		}
		visitor.endVisit(this);
	}

	public String toString() {
		return "." + subent;
	}

	public IDefUnitReference getRoot() {
		return null;
	}

	public EntitySingle getSubEnt() {
		return subent;
	}
	
	public DefUnit getTargetDefUnit() {
		IScopeNode scope = NodeUtil.getModule(this).getMembersScope();
		return EntityResolver.findDefUnitFromScope(scope, subent.name);
	}

}
