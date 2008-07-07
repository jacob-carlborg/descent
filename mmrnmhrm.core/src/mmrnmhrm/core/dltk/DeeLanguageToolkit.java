package mmrnmhrm.core.dltk;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeNameRules;
import mmrnmhrm.core.model.DeeNature;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.AbstractLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.environment.IEnvironment;

public class DeeLanguageToolkit extends AbstractLanguageToolkit  {

	private static final String DEE_LANGUAGE_CONTENT_DSOURCE = "mmrnmhrm.core.content.dsource";

	private static final IDLTKLanguageToolkit instance = new DeeLanguageToolkit();
	
	public static IDLTKLanguageToolkit getDefault() {
		return instance ;
	}
	
	public boolean languageSupportZIPBuildpath() {
		return true;
	}

	//@Override
	public String getLanguageName() {
		return "D";
	}

	//@Override
	public String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	//@Override
	public String getLanguageContentType() {
		return DEE_LANGUAGE_CONTENT_DSOURCE;
	}
	
	@Override
	public IStatus validateSourceModule(IResource resource) {
		String name = resource.getName();
		if(DeeNameRules.isValidCompilationUnitName(name)) {
			return Status.OK_STATUS;
		} else {
			return new Status(IStatus.ERROR, DeeCore.PLUGIN_ID, "Invalid resource name:" + name);
		}
	}
	
	@Override
	public boolean validateSourcePackage(IPath path, IEnvironment environment) {
		return true;
		//return DeeNameRules.isValidPackagePathName(path.toString());
	}

}
