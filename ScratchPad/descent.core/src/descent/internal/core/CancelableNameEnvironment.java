package descent.internal.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.internal.codeassist.ISearchRequestor;
import descent.internal.compiler.env.NameEnvironmentAnswer;
import descent.internal.compiler.problem.AbortCompilation;


public class CancelableNameEnvironment extends SearchableEnvironment {
	public IProgressMonitor monitor;

	public CancelableNameEnvironment(JavaProject project, WorkingCopyOwner owner, IProgressMonitor monitor) throws JavaModelException {
		super(project, owner);
		this.monitor = monitor;
	}

	private void checkCanceled() {
		if (this.monitor != null && this.monitor.isCanceled()) {
			if (NameLookup.VERBOSE)
				System.out.println(Thread.currentThread() + " CANCELLING LOOKUP "); //$NON-NLS-1$
			throw new AbortCompilation(true/*silent*/, new OperationCanceledException());
		}
	}

	public void findPackages(char[] prefix, ISearchRequestor requestor) {
		checkCanceled();
		super.findPackages(prefix, requestor);
	}

	public NameEnvironmentAnswer findType(char[] name, char[][] packageName) {
		checkCanceled();
		return super.findType(name, packageName);
	}

	public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
		checkCanceled();
		return super.findType(compoundTypeName);
	}

	public void findTypes(char[] prefix, boolean findMembers, boolean camelCaseMatch, ISearchRequestor storage) {
		checkCanceled();
		super.findTypes(prefix, findMembers, camelCaseMatch, storage);
	}
}
