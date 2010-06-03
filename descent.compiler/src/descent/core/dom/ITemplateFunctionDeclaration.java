package descent.core.dom;

import java.util.List;

public interface ITemplateFunctionDeclaration extends IFunctionDeclaration {
	
	List<TemplateParameter> templateParameters();
	Expression getConstraint();

}
