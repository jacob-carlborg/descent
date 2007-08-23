package dtool.dom.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.AnonDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.refmodel.INonScopedBlock;

public class DeclarationAnonMember extends ASTNeoNode implements INonScopedBlock {

	public NodeList body;

	public DeclarationAnonMember(AnonDeclaration node) {
		convertNode(node);
		this.body = NodeList.createNodeList(node.decl);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body.nodes);
		}
		visitor.endVisit(this);
	}

	public Iterator<ASTNeoNode> getMembersIterator() {
		return Arrays.asList(body.nodes).iterator();
	}

}
