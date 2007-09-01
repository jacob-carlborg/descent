package mmrnmhrm.ui.launch;

import mmrnmhrm.core.dltk.DeeLanguageToolkit;
import mmrnmhrm.core.model.DeeNature;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.debug.ui.launchConfigurations.MainLaunchConfigurationTab;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.swt.graphics.Image;

public class RubyMainLaunchConfigurationTab extends MainLaunchConfigurationTab {


	protected boolean validateProject(IScriptProject project) {
		if (project == null)
			return false;
		// check project nature		
		try {
			IDLTKLanguageToolkit ltk = DLTKLanguageManager.getLanguageToolkit(project);
			if (ltk instanceof DeeLanguageToolkit)
				return true;
		} catch (CoreException e) {
		}
		return false;
	}
	
	protected String getLanguageName () {
		return "RUBY";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_CLASS);
	}

	protected String getNatureID() {
		return DeeNature.NATURE_ID;
	}

}
