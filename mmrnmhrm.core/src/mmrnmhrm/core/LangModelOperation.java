package mmrnmhrm.core;

import java.util.ArrayList;
import java.util.List;

import mmrnmhrm.core.model.DeeModel;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A language model operation. TODO: register deltas. 
 */
public abstract class LangModelOperation implements IWorkspaceRunnable{

	/** The progress monitor passed into this operation.  */
	protected IProgressMonitor progressMonitor= null;
	/* Nested operation count */ 
	protected int operationCount;

	/* A per thread operations object (count). */
	protected static ThreadLocal<List<LangModelOperation>> operationStacks 
		= new ThreadLocal<List<LangModelOperation>>();

	/** Default constructor. */
	protected LangModelOperation() {
	}
	
	/** Performs the operation specific behavior. Subclasses must override. */
	protected abstract void executeOperation() throws LangModelException;

	/** Runs this operation. */
	public void run(IProgressMonitor monitor) throws CoreException {
		progressMonitor = monitor;
		try {
			pushOperation(this);
			executeOperation();
		} finally {
			popOperation();
			if(isTopLevelOperation()) {
				DeeModel.getInstance().fireModelChanged();
			}
		}
		
	}


	private boolean isTopLevelOperation() {
		return getCurrentOperationStack().size() == 0;
	}

	
	/**
	 * Returns the stack of operations running in the current thread.
	 * Returns an empty stack if no operations are currently running in this thread. 
	 */
	protected static List<LangModelOperation> getCurrentOperationStack() {
		List<LangModelOperation> stack = operationStacks.get();
		if (stack == null) {
			stack = new ArrayList<LangModelOperation>();
			operationStacks.set(stack);
		}
		return stack;
	}
	
	private void pushOperation(LangModelOperation operation) {
		getCurrentOperationStack().add(operation);
	}

	private void popOperation() {
		int size = getCurrentOperationStack().size();
		getCurrentOperationStack().remove(size-1);
	}

}
