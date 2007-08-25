package mmrnmhrm.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class ConfigureBuildPathHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		/*ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(!(selection instanceof IStructuredSelection))
			return null;

		IStructuredSelection sel = (IStructuredSelection) selection;
		IResource res;
		if(sel.getFirstElement() instanceof ILangElement)
			res = ((ILangElement) sel.getFirstElement()).getUnderlyingResource();
		else
			res = (IResource) sel.getFirstElement();
		
		IProject proj = res.getProject();

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(window.getShell(), proj,
				DeeBuildPathPropertyPage.PAGEID, null, null);
		
		dialog.open();
		*/
		return null;
	}

}
