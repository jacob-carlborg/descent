package descent.core.dom;

/**
 * A template delcaration:
 */
public interface ITemplateDeclaration extends IDeclaration {
	
	/**
	 * Returns the name of the template.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the template parameters.
	 */
	ITemplateParameter[] getTemplateParameters();
	
	/**
	 * Returns the declaration definitions contained in
	 * this template.
	 */
	IDeclaration[] getDeclarationDefinitions();

}
