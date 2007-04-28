package descent.internal.launching.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.ILineBreakpoint;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.JavaCore;

/**
 * Utility class for Descent breakpoints 
 */
public class BreakpointUtils {
	
	/**
	 * Returns the member associated with the line number of
	 * the given breakpoint.
	 * 
	 * @param breakpoint Descent line breakpoint
	 * @return member at the given line number in the type 
	 *  associated with the breakpoint
	 * @exception CoreException if an exception occurs accessing
	 *  the breakpoint
	 */
	public static IMember getMember(ILineBreakpoint breakpoint) throws CoreException {
		int start = breakpoint.getCharStart();
		int end = breakpoint.getCharEnd();
		
		if (start == -1 || end == -1) {
			return null;
		}
		
		int length = end - start;
		
		IResource resource = breakpoint.getMarker().getResource();
		IJavaElement element = JavaCore.create(resource);
		if (!(element instanceof ICompilationUnit)) {
			return null;
		}
		
		// TODO Descent debug: dosen't always match the desired function
		ICompilationUnit unit = (ICompilationUnit) element;
		IJavaElement elementAt = unit.getElementAt(start + length / 2);
		if (elementAt instanceof IMember) {
			return (IMember) elementAt;
		} else {
			return null;
		}
	}

}
