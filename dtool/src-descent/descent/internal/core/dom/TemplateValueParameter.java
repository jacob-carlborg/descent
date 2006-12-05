package descent.internal.core.dom;

import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;

public class TemplateValueParameter extends TemplateParameter {

	private final Identifier id;
	private final Type tp_valtype;
	private final Expression tp_specvalue;
	private final Expression tp_defaultvalue;

	public TemplateValueParameter(Identifier id, Type tp_valtype, Expression tp_specvalue, Expression tp_defaultvalue) {
		this.id = id;
		this.tp_valtype = tp_valtype;
		this.tp_specvalue = tp_specvalue;
		this.tp_defaultvalue = tp_defaultvalue;
	}
	
	public int getElementType() {
		return VALUE_TEMPLATE_PARAMETER;
	}
	
	public IName getName() {
		return id;
	}
	
	public IType getType() {
		return tp_valtype;
	}
	
	public IExpression getSpecificValue() {
		return tp_specvalue;
	}
	
	public IExpression getDefaultValue() {
		return tp_defaultvalue;
	}

	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, tp_valtype);
			acceptChild(visitor, id);
			acceptChild(visitor, tp_specvalue);
			acceptChild(visitor, tp_defaultvalue);
		}
		visitor.endVisit(this);
	}

}
