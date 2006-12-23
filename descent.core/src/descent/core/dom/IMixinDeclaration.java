package descent.core.dom;

/**
 * A mixin declaration:
 * 
 * <pre>
 * mixin foo.bar!(arg1, arg2, ..., argN) name;
 * </pre>
 */
public interface IMixinDeclaration extends IDeclaration {
	
	/**
	 * Returns the name of the mixin.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the qualified name of the mixin.
	 */
	IType getType();

}
