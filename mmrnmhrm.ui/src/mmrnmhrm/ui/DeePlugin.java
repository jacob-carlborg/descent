package mmrnmhrm.ui;

import java.util.HashMap;
import java.util.Map;

import melnorme.lang.ui.LangPlugin;
import melnorme.miscutil.log.Logg;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeNameRules;
import mmrnmhrm.ui.text.DeeCodeScanner;
import mmrnmhrm.ui.text.DeeDocumentProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;


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
		
		getInstance().cunitMap = new HashMap<IEditorInput, CompilationUnit>();

	}


	public static DeeDocumentProvider getDeeDocumentProvider() {
		return deeDocumentProvider;
	}
	
	public static synchronized DeeCodeScanner getDefaultDeeCodeScanner() {
		if(defaultDeeCodeScanner == null)
			defaultDeeCodeScanner = new DeeCodeScanner();
		return defaultDeeCodeScanner;
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
