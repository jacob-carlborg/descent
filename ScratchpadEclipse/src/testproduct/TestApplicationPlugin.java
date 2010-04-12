package testproduct;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class TestApplicationPlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "testproduct";
	
	
	protected static TestApplicationPlugin pluginInstance;
	
	public TestApplicationPlugin() {
		pluginInstance = this;
	}
	
	/** Returns the shared instance. */
	public static TestApplicationPlugin getInstance() {
		return pluginInstance;
	}
	
}
