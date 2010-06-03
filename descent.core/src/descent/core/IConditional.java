package descent.core;

public interface IConditional extends IMember, IJavaElement__Marker {
	
	IJavaElement[] getThenChildren() throws JavaModelException;
	
	IJavaElement[] getElseChildren() throws JavaModelException;
	
	boolean isDebugDeclaration() throws JavaModelException;
	
	boolean isVersionDeclaration() throws JavaModelException;
	
	boolean isIftypeDeclaration() throws JavaModelException;
	
	boolean isStaticIfDeclaration() throws JavaModelException;

}
