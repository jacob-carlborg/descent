package mmrnmhrm.core.model;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.lang.LangNature;

import org.eclipse.core.runtime.CoreException;

public class DeeNature extends LangNature  {

	public static final String NATURE_BASEID = "deenature";
	public static final String NATURE_ID = DeeCore.PLUGIN_ID +"."+ NATURE_BASEID;


	/** Configure the project with a Dee nature. */
	public void configure() throws CoreException {
		addToBuildSpec(DeeCore.BUILDER_ID);
	}
	
	/** Remove the Dee nature from the project. */
	public void deconfigure() throws CoreException {
		removeFromBuildSpec(DeeCore.BUILDER_ID);
	}

}
