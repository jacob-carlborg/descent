package descent.internal.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.ui.IWorkbenchWindow;

import descent.core.IJavaElement;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.util.OpenTypeHierarchyUtil;

/**
 * A command handler to show a java element in the type hierarchy view.
 * 
 * @since 3.2
 */
public class ShowElementInTypeHierarchyViewHandler extends AbstractHandler {

	private static final String PARAM_ID_ELEMENT_REF= "elementRef"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window= JavaPlugin.getActiveWorkbenchWindow();
		if (window == null)
			return null;

		IJavaElement javaElement= (IJavaElement) event.getObjectParameterForExecution(PARAM_ID_ELEMENT_REF);

		OpenTypeHierarchyUtil.open(javaElement, window);

		return null;
	}
}
