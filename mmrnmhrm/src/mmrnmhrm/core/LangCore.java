package mmrnmhrm.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * Lang Core
 */
public abstract class LangCore extends Plugin {


	protected static LangCore pluginInstance;
	

	/** @return the shared instance */
	/*public static LangCore getInstance() {
		return pluginInstance;
	}*/

	/** {@inheritDoc} */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPlugin();
	}

	/** {@inheritDoc} */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		pluginInstance = null;
	}


	
	/* *********************************************** */

	void initPlugin() throws CoreException {
	}
	
}
