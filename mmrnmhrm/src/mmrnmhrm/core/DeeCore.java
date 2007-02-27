package mmrnmhrm.core;

import mmrnmhrm.text.DeeCodeScanner_Native;
import mmrnmhrm.text.DeeDocumentProvider;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.rules.ITokenScanner;

public class DeeCore extends DeePluginActivator {

	private DeeDocumentProvider deeDocumentProvider;
	private ITokenScanner defaultDeeCodeScanner;
	
	
	public static ITokenScanner getDefaultDeeCodeScanner() {
		return getDefault().defaultDeeCodeScanner;
	}
	protected void initPlugin() {
		deeDocumentProvider = new DeeDocumentProvider();
		defaultDeeCodeScanner = new DeeCodeScanner_Native();
	}
	
	public static DeeDocumentProvider getDeeDocumentProvider() {
		return getDefault().deeDocumentProvider;
	}


	public static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

}
