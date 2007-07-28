package mmrnmhrm.core;

import mmrnmhrm.core.model.DeeModel;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;

import dtool.refmodel.EntityResolver;


public class DeeCore extends LangCore {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "mmrnmhrm.core";
	/** The builder ID */
	public final static String BUILDER_ID = PLUGIN_ID + ".deebuilder";

	public DeeCore() throws CoreException {
		pluginInstance = this;
		initPlugin();
	}
	

	@Override
	public void initPlugin() throws CoreException {
		EntityResolver.initializeEntityResolver(DeeModel.getRoot());
		DeeModel.initDeeModel();
	}


	public static void log(Exception e) {
		getInstance().getLog().log(new Status(IStatus.ERROR, DeeCore.PLUGIN_ID,
				ILangModelConstants.INTERNAL_ERROR,
				LangCoreMessages.LangCore_internal_error, e));
	}
}
