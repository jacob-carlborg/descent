package mmrnmhrm.ui;

import melnorme.swtutil.SWTUtilExt;
import mmrnmhrm.core.build.DeeProjectBuilder;
import mmrnmhrm.lang.ui.InitializeAfterLoadJob;
import mmrnmhrm.lang.ui.LangPlugin;
import mmrnmhrm.ui.launch.DeeBuilderUIListener;
import mmrnmhrm.ui.text.DeeTextTools;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.osgi.framework.BundleContext;

import dtool.Logg;


public class DeePlugin extends LangPlugin {

	public static final String PLUGIN_ID ="org.dsource.ddt.ide.ui";

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
		
		SWTUtilExt.enableDebugColorHelpers = Platform.inDebugMode();

		//defaultDeeCodeScanner = new DeeCodeScanner();
	}

	public static void initializeAfterLoad(IProgressMonitor monitor) throws CoreException {
		DeeProjectBuilder.setBuilderListener(new DeeBuilderUIListener());
		// nothing to do
		monitor.done();
	}

	public ScriptTextTools getTextTools() {
		if (fTextTools == null) {
			fTextTools = new DeeTextTools(true);
		}
		return fTextTools;
	}

}
