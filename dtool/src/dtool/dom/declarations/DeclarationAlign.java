package dtool.dom.declarations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.AlignDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.refmodel.INonScopedBlock;

public class DeclarationAlign extends ASTNeoNode implements INonScopedBlock {
	
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

	public Iterator<ASTNode> getMembersIterator() {
		return Arrays.asList(decls).iterator();
	}

}
