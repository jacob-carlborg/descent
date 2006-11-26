package descent.core.dom;

/**
 * A template instance type:
 * 
 * foo.Bar!(arg1, arg2, ..., argN)
 */
public interface ITemplateInstanceType extends IType {
	
	/**
	 * Returns the qualified name of the template type.
	 */
	IQualifiedName getName();
	
	/**
	 * Returns the short name of this type (in the example
	 * it would be "Bar").
	 */
	String getShortName();
	
	/**
	 * Returns the arguments of the template instance.
	 */
	IElement[] getTemplateArguments();

}
