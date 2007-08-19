package dtool.dom.definitions;

import java.util.Iterator;

import melnorme.miscutil.IteratorUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;
import dtool.dom.statements.IStatement;
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
	

	@SuppressWarnings("unchecked")
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
