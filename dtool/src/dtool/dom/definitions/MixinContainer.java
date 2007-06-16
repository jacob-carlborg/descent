package dtool.dom.definitions;

import util.tree.TreeVisitor;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Entity;
import dtool.refmodel.IDefinitionContainer;

public class MixinContainer extends ASTNeoNode implements IDefinitionContainer {

	public Entity type;
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}
	
	
	public ASTNode[] getMembers() {
		if(true) return null;
		DefUnit defunit = type.getTargetDefUnit();
		if(defunit == null)
			return null;
		return defunit.getMembersScope().getDefUnits().toArray(ASTNode.NO_ELEMENTS);
	}
}
