package descent.internal.launching.ui;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import descent.launching.ui.DescentLaunchingUI;

public class MainTab extends AbstractLaunchConfigurationTab {
	
	public void createControl(Composite parent) {
	}

	public String getName() {
		return "Main";
	}
	
	@Override
	public Image getImage() {
		return DescentLaunchingUI.getDefault().getImageRegistry().get(Images.MAIN_TAB);
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

}
