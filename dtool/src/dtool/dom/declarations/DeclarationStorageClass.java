package dtool.dom.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.StorageClassDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.refmodel.INonScopedBlock;

public class DeclarationStorageClass extends ASTNeoNode implements INonScopedBlock {

	public int stclass;
	public ASTNode[] decls;	// can be null?
	
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

	public ASTNode[] getMembers() {
		return decls;
	}

	public Iterator<ASTNode> getMembersIterator() {
		return Arrays.asList(getMembers()).iterator();
	}


}
