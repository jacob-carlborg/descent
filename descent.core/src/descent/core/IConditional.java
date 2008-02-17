package descent.core;

public interface IConditional extends IMember {
	
	boolean isDebugDeclaration() throws JavaModelException;
	
	boolean isVersionDeclaration() throws JavaModelException;
	
	boolean isIftypeDeclaration() throws JavaModelException;
	
	boolean isStaticIfDeclaration() throws JavaModelException;

}
