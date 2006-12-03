package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ISimpleName;
import descent.core.dom.ITupleTemplateParameter;

public class TemplateTupleParameter extends TemplateParameter implements ITupleTemplateParameter {

	private final Identifier ident;

	public TemplateTupleParameter(Identifier ident) {
		this.ident = ident;
	}
	
	public ISimpleName getName() {
		return ident;
	}

	public int getNodeType0() {
		return TUPLE_TEMPLATE_PARAMETER;
	}

	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
		}
		visitor.endVisit(this);
	}

}
