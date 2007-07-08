package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.properties.ProjConfigPropertyPage;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;

public class ConfigureBuildPathHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(!(selection instanceof IStructuredSelection))
			return null;

		IStructuredSelection sel = (IStructuredSelection) selection;
		IResource res = (IResource) sel.getFirstElement();
		IProject proj = res.getProject();

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(window.getShell(), proj,
				ProjConfigPropertyPage.PAGEID, null, null);
		
		dialog.open();
		return null;
	}

}
