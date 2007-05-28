package dtool.dom.declarations;

import util.tree.TreeVisitor;
import descent.internal.core.dom.PROT;
import descent.internal.core.dom.ProtDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationProtection extends ASTNeoNode {

	public PROT prot;
	public Declaration[] decls;	// can be null?
	
	public DeclarationProtection(ProtDeclaration elem) {
		convertNode(elem);
		this.prot = elem.prot;
		this.decls = Declaration.convertMany(elem.getDeclarationDefinitions());
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, prot);
			TreeVisitor.acceptChildren(visitor, decls);
		}
		visitor.endVisit(this);
	}

}
