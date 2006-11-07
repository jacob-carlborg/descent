package descent.core.dom;

/**
 * A template type parameter:
 * 
 * <pre>
 * name : specificType = defaultType
 * </pre>
 */
public interface ITemplateTypeParameter extends ITemplateParameter {
	
	/**
	 * Returns the name of the parameter.
	 */
	IName getName();
	
	/**
	 * Returns the specific type, if any, or null.
	 */
	IType getSpecificType();
	
	/**
	 * Returns the default type, if any, or null.
	 */
	IType getDefaultType();

}
