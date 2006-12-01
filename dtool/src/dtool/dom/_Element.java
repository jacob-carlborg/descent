package dtool.dom;

import dtool.dom.ext.ASTNeoVisitor;


public class _Element extends ASTElement {
	

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			// TODO: accept children
		}
		visitor.endVisit(this);
	}


}
