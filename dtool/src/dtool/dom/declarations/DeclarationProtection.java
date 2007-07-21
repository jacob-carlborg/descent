package dtool.dom.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.PROT;
import descent.internal.core.dom.ProtDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.refmodel.INonScopedBlock;

public class DeclarationProtection extends ASTNeoNode implements INonScopedBlock {

	public PROT prot;
	public ASTNode[] decls;	// can be null?
	
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
	
	public ASTNode[] getMembers() {
		return decls;
	}
	
	public Iterator<ASTNode> getMembersIterator() {
		return Arrays.asList(getMembers()).iterator();
	}



}
