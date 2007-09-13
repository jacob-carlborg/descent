package mmrnmhrm.ui;

import melnorme.lang.ui.InitializeAfterLoadJob;
import melnorme.lang.ui.LangPlugin;
import mmrnmhrm.core.build.DeeBuilder;
import mmrnmhrm.ui.launch.DeeBuildUIListener;
import mmrnmhrm.ui.text.DeeTextTools;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.osgi.framework.BundleContext;

import dtool.Logg;


public class DeePlugin extends LangPlugin {

	public static final String PLUGIN_ID ="mmrnmhrm.ui";

	public static boolean initialized; 
	protected static DeePlugin pluginInstance;

	/** Returns the plugin instance. */
	public static DeePlugin getInstance() {
		return getDefault();
	}
	
	/** Returns the plugin instance. */
	public static DeePlugin getDefault() {
		return pluginInstance;
	}
	
	private DeeTextTools fTextTools;

	public DeePlugin() {
		pluginInstance = this;
	}

	
	/** {@inheritDoc} */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPlugin();
		(new InitializeAfterLoadJob()).schedule();
	}
	
	/** {@inheritDoc} */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		pluginInstance = null;
	}
	
	public void initPlugin() throws CoreException {
		Logg.main.println(" =============  Mmrnmhrm INITIALIZING  ============= " );
		Logg.main.println("Location: " + Platform.getLocation());
		Logg.main.println("Instance Location: " + Platform.getInstanceLocation().getURL());

		//defaultDeeCodeScanner = new DeeCodeScanner();
	}

	public static void initializeAfterLoad(IProgressMonitor monitor) throws CoreException {
		DeeBuilder.setBuilderListener(new DeeBuildUIListener());
		// nothing to do
	}

	public ScriptTextTools getTextTools() {
		if (fTextTools == null) {
			fTextTools = new DeeTextTools(true);
		}

		return fTextTools;
	}

}
