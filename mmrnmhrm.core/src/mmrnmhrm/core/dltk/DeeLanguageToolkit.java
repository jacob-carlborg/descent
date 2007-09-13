package mmrnmhrm.core.dltk;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeNameRules;
import mmrnmhrm.core.model.DeeNature;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.AbstractLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;

public class DeeLanguageToolkit extends AbstractLanguageToolkit  {

	private static final String[] DEE_LANGUAGE_FILE_EXTENSIONS = new String[] {
		"d", "di", "dh"
	};

	private static final IDLTKLanguageToolkit instance = new DeeLanguageToolkit();
	
	public static IDLTKLanguageToolkit getDefault() {
		return instance ;
	}


	@Override
	protected String getCorePluginID() {
		return DeeCore.PLUGIN_ID;
	}

	@Override
	public String[] getLanguageFileExtensions() {
		return DEE_LANGUAGE_FILE_EXTENSIONS;
	}

	//@Override
	public String getLanguageName() {
		return "D";
	}

	//@Override
	public String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	public IStatus validateSourceModule(IModelElement parent, String name) {
		if(DeeNameRules.isValidCompilationUnitName(name)) {
			return new Status(IStatus.OK, DeeCore.PLUGIN_ID, null);
		} else {
			return new Status(IStatus.ERROR, DeeCore.PLUGIN_ID, 
					"Invalid resource name:" + name);
		}
	}
	
	
	
	/* TODO: when DLTK supports is, validate packages
	@Override
	public boolean validateSourcePackage(IPath path) {
		return DeeNameRules.isValidPackageName(path.lastSegment());
	}*/

}
