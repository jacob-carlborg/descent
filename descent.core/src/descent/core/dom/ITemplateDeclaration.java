package descent.core.dom;

/**
 * A template delcaration:
 */
public interface ITemplateDeclaration extends IDElement {
	
	/**
	 * Returns the name of the template.
	 */
	IName getName();
	
	/**
	 * Returns the template parameters.
	 */
	ITemplateParameter[] getTemplateParameters();
	
	/**
	 * Returns the declaration definitions contained in
	 * this template.
	 */
	IDElement[] getDeclarationDefinitions();

}
