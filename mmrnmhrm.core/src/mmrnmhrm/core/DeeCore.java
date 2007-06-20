package mmrnmhrm.core;

import mmrnmhrm.core.model.DeeModelManager;

import org.eclipse.core.runtime.CoreException;


public class DeeCore extends LangCore {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "mmrnmhrm";
	/** The builder ID */
	public final static String BUILDER_ID = PLUGIN_ID + ".deebuilder";

	public DeeCore() throws CoreException {
		pluginInstance = this;
		initPlugin();
	}
	

	@Override
	public void initPlugin() throws CoreException {
		DeeModelManager.initDeeModel();
	}
}
