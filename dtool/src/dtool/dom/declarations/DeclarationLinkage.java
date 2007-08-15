package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.LinkDeclaration;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationLinkage extends DeclarationAttrib {

	public LINK linkage;
	
	public DeclarationLinkage(LinkDeclaration elem) {
		super(elem, elem.decl);
		this.linkage = elem.linkage;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
