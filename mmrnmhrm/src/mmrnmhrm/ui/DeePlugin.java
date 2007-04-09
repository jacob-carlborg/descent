package mmrnmhrm.ui;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.ui.text.DeeCodeScanner;
import mmrnmhrm.ui.text.DeeDocumentProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import util.log.Logg;

public class DeePlugin extends LangPlugin {

	// Same id as the core, for now.
	public static final String PLUGIN_ID = DeeCore.PLUGIN_ID;

	public DeePlugin() {
		pluginInstance = this;
		//DeeCore.pluginInstance = new DeeCore();
	}
	
	/** @return the shared instance */
	public static DeePlugin getInstance() {
		return (DeePlugin) pluginInstance;
	}
	
	private DeeDocumentProvider deeDocumentProvider;
	private DeeCodeScanner defaultDeeCodeScanner;
	
	
	
	public static DeeDocumentProvider getDeeDocumentProvider() {
		return getInstance().deeDocumentProvider;
	}
	
	public static DeeCodeScanner getDefaultDeeCodeScanner() {
		return getInstance().defaultDeeCodeScanner;
	}
	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return DeePlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
	}
	
	public static Shell getActiveWorkbenchShell() {
		 IWorkbenchWindow window= getActiveWorkbenchWindow();
		 if (window != null) {
		 	return window.getShell();
		 }
		 return null;
	}
	

	protected void initPlugin() throws CoreException {
		Logg.println(" =============  Mmrnmhrm INITIALIZING  ============= " );
		Logg.println("Location: " + Platform.getLocation());
		Logg.println("Instance Location: " + Platform.getInstanceLocation().getURL());

		deeDocumentProvider = new DeeDocumentProvider();
		defaultDeeCodeScanner = new DeeCodeScanner();
		
		DeeModel.initDeeModel();
	}

	public static IPreferenceStore getPrefStore() {
		return getInstance().getPreferenceStore();
	}

}
