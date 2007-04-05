package mmrnmhrm.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public abstract class LangPlugin extends AbstractUIPlugin {

	protected static LangPlugin pluginInstance;
	
	
	/** @return the shared instance */
	public static LangPlugin getInstance() {
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
	}


	
	/* *********************************************** */

	void initPlugin() throws CoreException {
	}
}
