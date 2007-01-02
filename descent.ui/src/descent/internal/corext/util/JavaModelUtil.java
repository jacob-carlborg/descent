package descent.internal.corext.util;

import descent.core.ICompilationUnit;
import descent.core.JavaModelException;

public class JavaModelUtil {
	
	/**
	 * Force a reconcile of a compilation unit.
	 * @param unit
	 */
	public static void reconcile(ICompilationUnit unit) throws JavaModelException {
		unit.reconcile(
				ICompilationUnit.NO_AST, 
				false /* don't force problem detection */, 
				null /* use primary owner */, 
				null /* no progress monitor */);
	}
	
	/**
	 * Returns true if a cu is a primary cu (original or shared working copy)
	 */
	public static boolean isPrimary(ICompilationUnit cu) {
		return cu.getOwner() == null;
	}

}
