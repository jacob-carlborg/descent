package descent.core;

public interface IConditional extends IMember {
	
	boolean isDebugDeclaration() throws JavaModelException;
	
	boolean isVersionDeclaration() throws JavaModelException;
	
	boolean isIftypeDeclaration() throws JavaModelException;
	
	boolean isStaticIfDeclaration() throws JavaModelException;
	
	/**
	 * Returns Boolean.TRUE if this conditional is "true": then declarations
	 * are included.
	 * Returns Boolean.FALSE if this conditional is "false": else declarations
	 * are included.
	 * Returns null if this conditional evaluation result is unknown.
	 */
	Boolean isActive() throws JavaModelException;

}
