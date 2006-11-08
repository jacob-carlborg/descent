package descent.core.dom;

/**
 * A template tuple parameter:
 * 
 * <pre>
 * T ...
 * </pre>
 */
public interface ITemplateTupleParameter extends ITemplateParameter {
	
	/**
	 * Returns the name of this parameter.
	 */
	IName getName();

}
