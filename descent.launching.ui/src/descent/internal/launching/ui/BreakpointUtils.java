package descent.internal.launching.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.ILineBreakpoint;

import descent.core.IMember;

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
		/*
		int start = breakpoint.getCharStart();
		int end = breakpoint.getCharEnd();
		
		IType type = getType(breakpoint);
	
		if (start == -1 && end == -1) {
			start= breakpoint.getMarker().getAttribute(MEMBER_START, -1);
			end= breakpoint.getMarker().getAttribute(MEMBER_END, -1);
		}
		
		IMember member = null;
		if ((type != null && type.exists()) && (end >= start) && (start >= 0)) {
			member= binSearch(type, start, end);
		}
		if (member == null) {
			member= type;
		}
		return member;
		*/
		return null;
	}

}
