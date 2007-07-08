package mmrnmhrm.ui.actions;

import mmrnmhrm.core.model.DeeModelManager;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;


public class RefreshModelHandler extends AbstractHandler {


	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			DeeModelManager.getRoot().updateElementRecursive();
		} catch (CoreException ce) {
			throw new ExecutionException("RefreshModelHandler error", ce);
		}
		return null;
	}

}
