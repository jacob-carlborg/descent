package descent.internal.ui.infoviews;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.Assert;

import org.eclipse.ui.PlatformUI;

import descent.core.IJavaElement;

import descent.ui.actions.OpenAction;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPluginImages;

class GotoInputAction extends Action {

	private AbstractInfoView fInfoView;

	public GotoInputAction(AbstractInfoView infoView) {
		Assert.isNotNull(infoView);
		fInfoView= infoView;

		JavaPluginImages.setLocalImageDescriptors(this, "goto_input.gif"); //$NON-NLS-1$
		setText(InfoViewMessages.GotoInputAction_label);
		setToolTipText(InfoViewMessages.GotoInputAction_tooltip);
		setDescription(InfoViewMessages.GotoInputAction_description);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.OPEN_INPUT_ACTION);
	}

	public void run() {
		IJavaElement inputElement= fInfoView.getInput();
		new OpenAction(fInfoView.getViewSite()).run(new Object[] { inputElement });
	}
}
