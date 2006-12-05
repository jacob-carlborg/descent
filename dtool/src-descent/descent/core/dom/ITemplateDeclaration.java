package descent.core.dom;

import descent.internal.core.dom.Declaration;
import descent.internal.core.dom.TemplateParameter;

/**
 * A template delcaration:
 */
public interface ITemplateDeclaration extends IDeclaration {
	
	/**
	 * Returns the name of the template.
	 */
	IName getName();
	
	/**
	 * Returns the template parameters.
	 */
	TemplateParameter[] getTemplateParameters();
	
	/**
	 * Returns the declaration definitions contained in
	 * this template.
	 */
	IDeclaration[] getDeclarationDefinitions();

}
