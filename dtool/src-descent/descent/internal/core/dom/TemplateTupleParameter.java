package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IName;
import descent.core.domX.ASTVisitor;

public class TemplateTupleParameter extends TemplateParameter {

	private final Identifier ident;

	public TemplateTupleParameter(Identifier ident) {
		this.ident = ident;
	}
	
	public IName getName() {
		return ident;
	}

	public int getElementType() {
		return ElementTypes.TUPLE_TEMPLATE_PARAMETER;
	}

	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
