package dtool.ast.definitions;

import java.util.Iterator;

import melnorme.miscutil.IteratorUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public class MixinContainer extends ASTNeoNode implements IStatement, INonScopedBlock {

	public final Reference type;
	
	public MixinContainer(TemplateMixin node, Reference typeref) {
		convertNode(node);
		type = typeref;
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Iterator<ASTNode> getMembersIterator() {
		return IteratorUtil.getEMPTY_ITERATOR();
		// TODO: mixin container
		/*
		DefUnit defunit = type.findTargetDefUnit();
		if(defunit == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		return (Iterator) defunit.getMembersScope().getMembersIterator();
		 */
	}
}
