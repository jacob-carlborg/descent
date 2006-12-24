package descent.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import descent.core.dom.IParser;
import descent.internal.core.parser.ParserFacade;

/**
 * The activator class controls the plug-in life cycle
 */
public class DescentCore extends Plugin {
	
	// The plug-in ID
	public static final String PLUGIN_ID = "descent.core";

	// The shared instance
	private static DescentCore plugin;
	
	private IParser parser;
	
	/**
	 * The constructor
	 */
	public DescentCore() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DescentCore getDefault() {
		return plugin;
	}
	
	/**
	 * Returns a IParser for this plugin.
	 */
	public IParser getParser() {
		if (parser == null) {
			parser = new ParserFacade();
		}
		return parser;
	}

}
