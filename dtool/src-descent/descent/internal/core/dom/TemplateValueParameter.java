package descent.internal.core.dom;

import util.tree.TreeVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.domX.IASTVisitor;

public class TemplateValueParameter extends TemplateParameter {

	public final Identifier id;
	public final Type tp_valtype;
	public final Expression tp_specvalue;
	public final Expression tp_defaultvalue;

	public TemplateValueParameter(Identifier id, Type tp_valtype, Expression tp_specvalue, Expression tp_defaultvalue) {
		this.id = id;
		this.tp_valtype = tp_valtype;
		this.tp_specvalue = tp_specvalue;
		this.tp_defaultvalue = tp_defaultvalue;
	}
	
	public int getElementType() {
		return ElementTypes.VALUE_TEMPLATE_PARAMETER;
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

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, tp_valtype);
			TreeVisitor.acceptChild(visitor, id);
			TreeVisitor.acceptChild(visitor, tp_specvalue);
			TreeVisitor.acceptChild(visitor, tp_defaultvalue);
		}
		visitor.endVisit(this);
	}

}
