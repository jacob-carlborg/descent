package mmrnmhrm.ui.actions;

import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.ui.DeeUI;
import mmrnmhrm.ui.wizards.projconfig.ProjectConfigBlock;

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
	
	class Foo extends TrayDialog {
		private ProjectConfigBlock fProjCfg;
		
		protected Foo(Shell shell) {
			super(shell);
			setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
			fProjCfg = new ProjectConfigBlock();
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			fProjCfg.init(DeeModelManager.getLangProject("DeeProj"));
			return fProjCfg.createControl(parent);		
		}
		
	}

	public void init(IWorkbenchWindow window) {
	}

	public void dispose() {
	}

	public void run(IAction action) {

				   
	   Foo foo = new Foo(DeeUI.getActiveWorkbenchShell());
		foo.open();
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}

}
