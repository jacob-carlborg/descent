package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IName;
import descent.core.dom.IAliasTemplateParameter;
import descent.core.dom.IType;

public class TemplateAliasParameter extends TemplateParameter implements IAliasTemplateParameter {

	private final Identifier tp_ident;
	private final Type tp_spectype;
	private final Type tp_defaulttype;

	public TemplateAliasParameter(Loc loc, Identifier tp_ident, Type tp_spectype, Type tp_defaulttype) {
		this.tp_ident = tp_ident;
		this.tp_spectype = tp_spectype;
		this.tp_defaulttype = tp_defaulttype;
	}
	
	public int getElementType() {
		return ALIAS_TEMPLATE_PARAMETER;
	}
	
	public IName getName() {
		return tp_ident;
	}	

	public IType getSpecificType() {
		return tp_spectype;
	}
	
	public IType getDefaultType() {
		return tp_defaulttype;
	}

	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, tp_ident);
			acceptChild(visitor, tp_spectype);
			acceptChild(visitor, tp_defaulttype);
		}
		visitor.endVisit(this);
	}

}
