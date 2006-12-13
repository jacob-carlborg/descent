package descent.internal.core.dom;

import descent.core.dom.IName;
import descent.core.dom.IElement.ElementTypes;
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
			acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
