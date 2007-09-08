package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.AlignDeclaration;
import dtool.dom.ast.IASTNeoVisitor;

public class DeclarationAlign extends DeclarationAttrib {
	
	public long alignnum;

	public DeclarationAlign(AlignDeclaration elem) {
		super(elem, elem.decl);
		this.alignnum = elem.salign;
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
		return "[align("+alignnum+")]";
	}

}
