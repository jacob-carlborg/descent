package descent.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.TableViewer;

import org.eclipse.ui.PlatformUI;

import descent.internal.ui.IJavaHelpContextIds;
 
/**
 * This action selects all entries currently showing in view.
 */
public class SelectAllAction extends Action {

	private TableViewer fViewer;

	/**
	 * Creates the action.
	 */
	public SelectAllAction(TableViewer viewer) {
		super("selectAll"); //$NON-NLS-1$
		setText(ActionMessages.SelectAllAction_label); 
		setToolTipText(ActionMessages.SelectAllAction_tooltip); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.SELECT_ALL_ACTION);
		Assert.isNotNull(viewer);
		fViewer= viewer;
	}

	/**
	 * Selects all resources in the view.
	 */
	public void run() {
		fViewer.getTable().selectAll();
		// force viewer selection change
		fViewer.setSelection(fViewer.getSelection());
	}
}
