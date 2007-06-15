package dtool.dom.declarations;

import util.tree.TreeVisitor;
import descent.internal.core.dom.AlignDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.model.IDefinitionContainer;

public class DeclarationAlign extends ASTNeoNode implements IDefinitionContainer {
	
	public long alignnum;
	public ASTNode[] decls;

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

	public ASTNode[] getMembers() {
		return decls;
	}

}
