package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IName;
import descent.core.dom.ITupleTemplateParameter;

public class TemplateTupleParameter extends TemplateParameter implements ITupleTemplateParameter {

	private final Identifier ident;

	public TemplateTupleParameter(Loc loc, Identifier ident) {
		this.ident = ident;
	}
	
	public IName getName() {
		return ident;
	}

	public int getElementType() {
		return TUPLE_TEMPLATE_PARAMETER;
	}

	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
