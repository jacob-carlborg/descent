package descent.internal.core.dom;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ISimpleName;
import descent.core.dom.ITypeTemplateParameter;
import descent.core.dom.IType;

public class TemplateTypeParameter extends TemplateParameter implements ITypeTemplateParameter {

	private final Identifier ident;
	private final DmdType tp_spectype;
	private final DmdType tp_defaulttype;

	public TemplateTypeParameter(Identifier ident, DmdType tp_spectype, DmdType tp_defaulttype) {
		this.ident = ident;
		this.tp_spectype = tp_spectype;
		this.tp_defaulttype = tp_defaulttype;
	}
	
	public int getNodeType0() {
		return TYPE_TEMPLATE_PARAMETER;
	}
	
	public ISimpleName getName() {
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
