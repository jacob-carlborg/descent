package descent.core.dom;

/**
 * A mixin declaration:
 * 
 * <pre>
 * mixin foo.bar!(arg1, arg2, ..., argN) name;
 * </pre>
 */
public interface IMixinDeclaration extends IDElement {
	
	/**
	 * Returns the name of the mixin.
	 */
	IName getName();
	
	/**
	 * Returns the qualified name of the mixin.
	 */
	IQualifiedName getType();
	
	/**
	 * Returns the template arguments of the mixin.
	 */
	IDElement[] getTemplateArguments();

}
