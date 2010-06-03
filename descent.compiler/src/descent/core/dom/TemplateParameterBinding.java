package descent.core.dom;

import descent.core.IJavaElement;
import descent.internal.compiler.parser.TemplateParameter;

public class TemplateParameterBinding extends AbstractBinding  implements ITemplateParameterBinding {
	
	private final TemplateParameter param;
	private final DefaultBindingResolver bindingResolver;

	public TemplateParameterBinding(descent.internal.compiler.parser.TemplateParameter param, DefaultBindingResolver bindingResolver) {
		this.param = param;
		this.bindingResolver = bindingResolver;		
	}

	public IJavaElement getJavaElement() {
		return null;
	}

	public String getKey() {
		return param.getSignature();
	}

	public int getKind() {
		return TEMPLATE_PARAMETER;
	}

	public long getModifiers() {
		return 0;
	}

	public String getName() {
		return param.ident.toString();
	}

	public boolean isDeprecated() {
		return false;
	}

	public boolean isSynthetic() {
		return false;
	}

}
