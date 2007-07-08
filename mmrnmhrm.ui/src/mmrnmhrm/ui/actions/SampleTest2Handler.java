package mmrnmhrm.ui.actions;

import melnorme.lang.ui.ExceptionHandler;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.ui.wizards.projconfig.ProjectConfigBlock;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;


public class SampleTest2Handler extends AbstractHandler {

	public static class TestDialog extends TrayDialog {
		private ProjectConfigBlock fProjCfg;
		
		protected TestDialog(Shell shell) {
			super(shell);
			setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
			fProjCfg = new ProjectConfigBlock();
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			fProjCfg.init(DeeModelManager.getRoot().getDeeProjects()[0]);
			Control control = fProjCfg.createControl(parent); 
			control.setLayoutData(new GridData(GridData.FILL_BOTH));
			return control;
		}
		
		@Override
		public int open() {
			int ret = super.open();
			try {
				IWorkspaceRunnable op = new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						fProjCfg.applyConfig();
					}
				};
				DeeCore.run(op, null);
			} catch (CoreException e) {
				ExceptionHandler.handle(e, "D Project Config Error", "Error saving project settings.");
				return ret;
			}
			return ret;
		}
		
	}


	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		TestDialog foo = new TestDialog(window.getShell());
		foo.open();
		return null;
	}

}
