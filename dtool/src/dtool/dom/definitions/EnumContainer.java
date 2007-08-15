package dtool.dom.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.ASTNode;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;
import dtool.dom.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public class EnumContainer extends ASTNeoNode implements IStatement, INonScopedBlock {

	public List<EnumMember> members;
	public Reference type;
	

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);	

	}

	public ASTNode[] getMembers() {
		return members.toArray(ASTNode.NO_ELEMENTS);
	}

	public Iterator<EnumMember> getMembersIterator() {
		return members.iterator();
	}

}
