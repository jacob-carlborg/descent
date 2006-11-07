package descent.internal.core.dom;

import descent.core.dom.IDElementVisitor;

public class TemplateValueParameter extends TemplateParameter {

	public TemplateValueParameter(Loc loc, Identifier tp_ident, Type tp_valtype, Expression tp_specvalue, Expression tp_defaultvalue) {
		// TODO Auto-generated constructor stub
	}
	
	public int getTemplateParameterType() {
		return TEMPLATE_PARAMETER_VALUE;
	}

	public void accept(IDElementVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

}
