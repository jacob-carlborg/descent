package dtool.dom.declarations;

import java.util.Iterator;

import melnorme.miscutil.Assert;
import melnorme.miscutil.IteratorUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.ProtDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationProtection extends DeclarationAttrib {

	public Modifier modifier;
	public PROT prot;
	
	public DeclarationProtection(ProtDeclaration elem) {
		super(elem, elem.decl);
		this.modifier = elem.modifier;
		this.prot = elem.protection;
		Assert.isTrue(PROT.fromTOK(this.modifier.tok) == this.prot);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, prot);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	public Iterator<ASTNeoNode> getMembersIterator() {
		return IteratorUtil.singletonIterator(body);
	}

}
