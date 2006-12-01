package descent.core.dom;

/**
 * A debug conditional declaration.
 */
public interface IDebugDeclaration extends IConditionalDeclaration {

	/**
	 * Returns the debug lever or identifier.
	 */
	ISimpleName getDebug();

}