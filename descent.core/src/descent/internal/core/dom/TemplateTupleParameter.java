package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IName;
import descent.core.dom.ITemplateTupleParameter;

public class TemplateTupleParameter extends TemplateParameter implements ITemplateTupleParameter {

	private final Identifier ident;

	public TemplateTupleParameter(Loc loc, Identifier ident) {
		this.ident = ident;
	}
	
	public IName getName() {
		return ident;
	}

	public int getTemplateParameterType() {
		return TEMPLATE_PARAMETER_TUPLE;
	}

	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
