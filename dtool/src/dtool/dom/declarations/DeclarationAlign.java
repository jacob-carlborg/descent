package dtool.dom.declarations;

import util.tree.TreeVisitor;
import descent.internal.core.dom.AlignDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationAlign extends ASTNeoNode {
	
	public ASTNode[] decls;
	public long alignnum;

	public DeclarationAlign(AlignDeclaration elem) {
		setSourceRange(elem);
		decls = Declaration.convertMany(elem.getDeclarationDefinitions());
		this.alignnum = elem.n;
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
