package org.eclipse.jdt.internal.ui;


import mmrnmhrm.ui.DeePlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * JDT STUB/Adaptor for JavaPlugin
 */
public class JavaPlugin extends DeePlugin {
	
	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}
	
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getPluginId(), IJavaStatusConstants.INTERNAL_ERROR, JavaUIMessages.JavaPlugin_internal_error, e)); 
	}

	public static String getPluginId() {
		return DeePlugin.PLUGIN_ID;
	}
}
