package mmrnmhrm.ui;

import java.util.HashMap;
import java.util.Map;

import melnorme.lang.ui.InitializeAfterLoadJob;
import melnorme.lang.ui.LangPlugin;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeNameRules;
import mmrnmhrm.ui.text.DeeDocumentProvider;
import mmrnmhrm.ui.text.DeeTextTools;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.BundleContext;


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
	private static DeeDocumentProvider deeDocumentProvider;
	
	private DeeTextTools fTextTools;

	public DeePlugin() {
		pluginInstance = this;
	}

	
	/** {@inheritDoc} */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPlugin();
		(new InitializeAfterLoadJob()).schedule();
	}
	
	/** {@inheritDoc} */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		pluginInstance = null;
	}
	
	public void initPlugin() throws CoreException {
		Logg.main.println(" =============  Mmrnmhrm INITIALIZING  ============= " );
		Logg.main.println("Location: " + Platform.getLocation());
		Logg.main.println("Instance Location: " + Platform.getInstanceLocation().getURL());

		deeDocumentProvider = new DeeDocumentProvider();
		//defaultDeeCodeScanner = new DeeCodeScanner();
		
		getInstance().cunitMap = new HashMap<IEditorInput, CompilationUnit>();

	}

	public static void initializeAfterLoad(IProgressMonitor monitor) throws CoreException {
		// nothing to do
	}

	public static DeeDocumentProvider getDeeDocumentProvider() {
		return deeDocumentProvider;
	}
	
	
	public ScriptTextTools getTextTools() {
		if (fTextTools == null) {
			fTextTools = new DeeTextTools(true);
		}

		return fTextTools;
	}

	protected Map<IEditorInput, CompilationUnit> cunitMap;

	/** Returns the Compilation for the given file. If the file is not part
	 * of the model, returns an out of model Compilation Unit.*/
	public CompilationUnit getCompilationUnit(IEditorInput input) throws CoreException {
		CompilationUnit cunit = null;
		if(input instanceof FileEditorInput) {
			IFile file = ((FileEditorInput) input).getFile();
			cunit = DeeModel.findCompilationUnit(file);
			if(cunit == null) {
				if(cunitMap.containsKey(input))
					return cunitMap.get(input);
				
				if(!DeeNameRules.isValidCompilationUnitName(file.getName()))
					return null;
					
				cunit = new CompilationUnit(file);
				cunit.createElementInfo();
				cunitMap.put(input, cunit);
			}
		}
		return cunit;
	}
	
	public static CompilationUnit getCompilationUnitOperation(IEditorInput input) {
		try {
			//throw new CoreException(new Status(IStatus.ERROR,"ASD", "messagE"));
			return DeePlugin.getInstance().getCompilationUnit(input);
		} catch (CoreException ce) {
			LangPlugin.log(ce);
			return null;
		}
	}



}
