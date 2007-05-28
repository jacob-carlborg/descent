package dtool.dom.declarations;

import util.tree.TreeVisitor;
import descent.internal.core.dom.StorageClassDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationStorageClass extends ASTNeoNode {

	public int stclass;
	public Declaration[] decls;	// can be null?
	
	public DeclarationStorageClass(StorageClassDeclaration elem) {
		convertNode(elem);
		this.stclass = elem.stc;
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
