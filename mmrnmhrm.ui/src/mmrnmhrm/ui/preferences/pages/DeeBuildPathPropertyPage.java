package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.preferences.DeeBuildpathsBlock;

import org.eclipse.dltk.ui.preferences.BuildPathsPropertyPage;
import org.eclipse.dltk.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.dltk.ui.wizards.BuildpathsBlock;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class DeeBuildPathPropertyPage extends BuildPathsPropertyPage implements IWorkbenchPropertyPage {

	public static final String PAGEID = "mmrnmhrm.ui.properties.DeeBuildPathProperties";  

	protected BuildpathsBlock createBuildPathBlock(IWorkbenchPreferenceContainer pageContainer) {
		return new DeeBuildpathsBlock(new BusyIndicatorRunnableContext(), 
				this, getSettings().getInt(INDEX), false, pageContainer);
	}
}