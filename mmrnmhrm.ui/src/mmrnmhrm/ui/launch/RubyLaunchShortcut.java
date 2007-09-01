package mmrnmhrm.ui.launch;

import mmrnmhrm.core.launch.DeeLaunchConfigurationConstants;
import mmrnmhrm.core.model.DeeNature;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut;

// TODO: adapt for singleton launch
public class RubyLaunchShortcut extends AbstractScriptLaunchShortcut {
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(
				DeeLaunchConfigurationConstants.ID_DEE_SCRIPT);
	}

	protected String getNatureId() {
		return DeeNature.NATURE_ID;
	}
}
