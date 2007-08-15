package dtool.dom.declarations;

import java.util.Iterator;

import melnorme.miscutil.IteratorUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.AnonDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.statements.MultiNodes;
import dtool.refmodel.INonScopedBlock;

public class DeclarationAnonMember extends ASTNeoNode implements INonScopedBlock {

	public ASTNeoNode body;

	public DeclarationAnonMember(AnonDeclaration node) {
		convertNode(node);
		this.body = MultiNodes.createNodeBlock(node.decl);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

	public Iterator<ASTNeoNode> getMembersIterator() {
		return IteratorUtil.singletonIterator(body);
	}

}
