package descent.core.dom;

/**
 * A template value parameter.
 * 
 * <pre>
 * type name : specificValue = defaultValue
 * </pre>
 */
public interface IValueTemplateParameter extends ITemplateParameter {
	
	/**
	 * Returns the type of this parameter.
	 */
	IType getType();
	
	/**
	 * Returns the specific value of this parameter.
	 */
	IExpression getSpecificValue();
	
	/**
	 * Returns the default value of this parameter.
	 */
	IExpression getDefaultValue();

}
