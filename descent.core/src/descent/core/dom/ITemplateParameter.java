package descent.core.dom;

/**
 * A template parameter.
 */
public interface ITemplateParameter extends IDElement {
	
	/**
	 * Constant representing a value template parameter.
	 * A template parameter with this type can be safely cast to <code>ITemplateValueParameter</code>.
	 */
	int TEMPLATE_PARAMETER_VALUE = 1;
	
	/**
	 * Constant representing a type template parameter.
	 * A template parameter with this type can be safely cast to <code>ITemplateTypeParameter</code>. 
	 */
	int TEMPLATE_PARAMETER_TYPE = 2;
	
	/**
	 * Constant representing an alias template parameter.
	 * A template parameter with this type can be safely cast to <code>ITemplateAliasParameter</code>. 
	 */
	int TEMPLATE_PARAMETER_ALIAS = 3;
	
	/**
	 * Constant representing a tuple template parameter.
	 * A template parameter with this type can be safely cast to <code>ITemplateTupleParameter</code>. 
	 */
	int TEMPLATE_PARAMETER_TUPLE = 4;
	
	/**
	 * Returns the type of this template parameter. Check the constants in
	 * this interace.
	 */
	int getTemplateParameterType();

}
