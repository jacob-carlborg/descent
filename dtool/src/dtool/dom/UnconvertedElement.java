package dtool.dom;

import descent.core.domX.ASTNode;
import descent.core.domX.AbstractElement;
import descent.internal.core.dom.Type;
import dtool.dom.ext.ASTNeoVisitor;


public class UnconvertedElement extends ASTElement {
	
	public AbstractElement unkelem;

	public UnconvertedElement(AbstractElement elem) {
		this.unkelem = elem;
	}

	public void accept0(ASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, unkelem);
		}
		visitor.endVisit(this);
	}
	
}
