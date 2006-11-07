package descent.internal.core.dom;

import descent.core.dom.ITemplateParameter;

public abstract class TemplateParameter extends AbstractElement implements ITemplateParameter {
	
	public final int getElementType() {
		return TEMPLATE_PARAMETER;
	}

}
