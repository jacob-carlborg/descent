package dtool.dom.declarations;

import util.tree.TreeVisitor;
import descent.internal.core.dom.LinkDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationLinkage extends ASTNeoNode {

	public int linkage;
	public Declaration[] decls;
	
	public DeclarationLinkage(LinkDeclaration elem) {
		this.linkage = elem.getLinkage();
		this.decls = Declaration.convertMany(elem.getDeclarationDefinitions());
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, decls);
		}
		visitor.endVisit(this);
	}

}
