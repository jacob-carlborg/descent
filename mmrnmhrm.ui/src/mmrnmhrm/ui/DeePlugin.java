package mmrnmhrm.ui;

import melnorme.lang.ui.LangPlugin;
import mmrnmhrm.ui.text.DeeCodeScanner;
import mmrnmhrm.ui.text.DeeDocumentProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;

import util.log.Logg;

public class DeePlugin extends LangPlugin {

	// Same id as the core, for now.
	public static final String PLUGIN_ID ="mmrnmhrm.ui";

	/** Returns the plugin instance. */
	public static DeePlugin getInstance() {
		return (DeePlugin) pluginInstance;
	}
	
	public DeePlugin() {
		pluginInstance = this;
	}

	private static DeeDocumentProvider deeDocumentProvider;
	private static DeeCodeScanner defaultDeeCodeScanner;
	
	
	public void initPlugin() throws CoreException {
		Logg.main.println(" =============  Mmrnmhrm INITIALIZING  ============= " );
		Logg.main.println("Location: " + Platform.getLocation());
		Logg.main.println("Instance Location: " + Platform.getInstanceLocation().getURL());

		deeDocumentProvider = new DeeDocumentProvider();
		//defaultDeeCodeScanner = new DeeCodeScanner();
	}

	public static DeeDocumentProvider getDeeDocumentProvider() {
		return deeDocumentProvider;
	}
	
	public static DeeCodeScanner getDefaultDeeCodeScanner() {
		if(defaultDeeCodeScanner == null)
			defaultDeeCodeScanner = new DeeCodeScanner();
		return defaultDeeCodeScanner;
	}
}
