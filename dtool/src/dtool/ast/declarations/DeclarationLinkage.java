package dtool.ast.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.LinkDeclaration;
import dtool.ast.IASTNeoVisitor;

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
			TreeVisitor.acceptChildren(visitor, body.nodes);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "[extern("+linkage+")]";
	}

}
