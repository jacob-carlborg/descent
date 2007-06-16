package descent.ui.text.java;

import org.eclipse.core.runtime.CoreException;

import descent.core.ICompilationUnit;


/**
 * Interface to be implemented by contributors to the extension point
 * <code>descent.ui.quickFixProcessors</code>.
 * <p>Since 3.2, each extension specifies the marker types it can handle, and {@link #hasCorrections(ICompilationUnit, int)} and
 * {@link #getCorrections(IInvocationContext, IProblemLocation[])} are called if (and only if) quick fix is
 * required for a problem of these types.</p>
 * <p>Note, if a extension does not specify marker types it will be only called for problem of type
 * <code>descent.core.problem</code>, <code>descent.core.buildpath_problem</code>
 * and <code>descent.core.task</code>; compatible with the behavior prior to 3.2</p>
 * 
 * @since 3.0
 */
public interface IQuickFixProcessor {

	/**
	 * Returns <code>true</code> if the processor has proposals for the given problem. This test should be an
	 * optimistic guess and be very cheap.
	 *
	 * @param unit the compilation unit
	 * @param problemId the problem Id. The id is of a problem of the problem type(s) this processor specified in
	 * the extension point.
	 * @return <code>true</code> if the processor has proposals for the given problem
	 */
	boolean hasCorrections(ICompilationUnit unit, int problemId);

	/**
	 * Collects corrections or code manipulations for the given context.
	 *
	 * @param context Defines current compilation unit, position and a shared AST
	 * @param locations Problems are the current location.
	 * @return the corrections applicable at the location or <code>null</code> if no proposals
	 * 			can be offered
	 * @throws CoreException CoreException can be thrown if the operation fails
	 */
	IJavaCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations) throws CoreException;

}
