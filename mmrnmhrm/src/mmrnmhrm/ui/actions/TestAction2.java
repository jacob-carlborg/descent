package mmrnmhrm.ui.actions;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.LangModelException;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.ui.DeeUI;
import mmrnmhrm.ui.ExceptionHandler;
import mmrnmhrm.ui.wizards.projconfig.ProjectConfigBlock;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.IJavaStatusConstants;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class TestAction2 implements IWorkbenchWindowActionDelegate {
	
	class TestDialog extends TrayDialog {
		private ProjectConfigBlock fProjCfg;
		
		protected TestDialog(Shell shell) {
			super(shell);
			setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
			fProjCfg = new ProjectConfigBlock();
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			fProjCfg.init(DeeModelManager.getLangProject("DeeProj"));
			return fProjCfg.createControl(parent);		
		}
		
		@Override
		public int open() {
			int ret = super.open();
			try {
				DeeCore.run(new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						// TODO: Status, err
						throw new LangModelException(new Status(IStatus.ERROR, JavaPlugin.getPluginId(), 
								IJavaStatusConstants.INTERNAL_ERROR, "Status Message", new NullPointerException()));
						//fProjCfg.saveConfig();
					}
				}, null);
			} catch (CoreException e) {
				ExceptionHandler.handle(e, "D Project Config Error", "Error saving project settings.");
				return ret;
			}
			return ret;
		}
		
	}

	public void init(IWorkbenchWindow window) {
	}

	public void dispose() {
	}

	public void run(IAction action) {
				  
		TestDialog foo = new TestDialog(DeeUI.getActiveWorkbenchShell());
		foo.open();
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}

}
