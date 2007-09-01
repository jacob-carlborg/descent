package mmrnmhrm.core;

import mmrnmhrm.core.dltk.DLTKModuleResolver;
import mmrnmhrm.core.model.DeeModel;

import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.BundleContext;

import dtool.refmodel.EntityResolver;


public class DeeCore extends LangCore {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "mmrnmhrm.core";
	/** The builder ID */
	public final static String BUILDER_ID = PLUGIN_ID + ".deebuilder";
	
	protected static DeeCore pluginInstance;
	
	public DeeCore() {
		pluginInstance = this;
	}
	
	/** Returns the shared instance. */
	public static DeeCore getInstance() {
		return pluginInstance;
	}

	/** {@inheritDoc} */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPlugin();
	}

	/** {@inheritDoc} */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		pluginInstance = null;
		DeeModel.dispose();
	}

	
	public void initPlugin() throws CoreException {
		//EntityResolver.initializeEntityResolver(DeeModel.getRoot());
		EntityResolver.initializeEntityResolver(DLTKModuleResolver.instance);
		//TypeHierarchy.DEBUG = true;
		
		DeeModel.initModel(); // Can we add a listener here?
	}

/*	public static Preferences getPreferences() {
		return getInstance().getPluginPreferences();
	}
*/

	/* *********************************************** */


}
