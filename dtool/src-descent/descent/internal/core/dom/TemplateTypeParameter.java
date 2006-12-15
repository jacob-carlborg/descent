package descent.internal.core.dom;

import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;

public class TemplateTypeParameter extends TemplateParameter {

	private final Identifier ident;
	private final Type tp_spectype;
	private final Type tp_defaulttype;

	public TemplateTypeParameter(Identifier ident, Type tp_spectype, Type tp_defaulttype) {
		this.ident = ident;
		this.tp_spectype = tp_spectype;
		this.tp_defaulttype = tp_defaulttype;
	}
	
	public int getElementType() {
		return ElementTypes.TYPE_TEMPLATE_PARAMETER;
	}
	
	public IName getName() {
		return ident;
	}
	
	public IType getSpecificType() {
		return tp_spectype;
	}
	
	public IType getDefaultType() {
		return tp_defaulttype;
	}

	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChild(visitor, tp_spectype);
			acceptChild(visitor, tp_defaulttype);
		}
		visitor.endVisit(this);
	}

}
