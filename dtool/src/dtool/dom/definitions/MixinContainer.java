package dtool.dom.definitions;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.ASTNode;
import descent.core.domX.ASTRangeLessNode;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;
import dtool.refmodel.INonScopedBlock;

public class MixinContainer extends ASTNeoNode implements INonScopedBlock {

	public Reference type;
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}
	
	
	public ASTRangeLessNode[] getMembers() {
		return null;
		/*
		DefUnit defunit = type.findTargetDefUnit();
		if(defunit == null)
			return null;
		return defunit.getMembersScope().getDefUnits().toArray(ASTNode.NO_ELEMENTS);
		*/
	}


	public Iterator<ASTNode> getMembersIterator() {
		List<ASTNode> empty = Collections.emptyList();
		return empty.iterator();
				
	}
}
