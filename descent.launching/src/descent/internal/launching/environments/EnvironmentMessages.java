package descent.internal.launching.environments;

import org.eclipse.osgi.util.NLS;

/**
 * @since 3.2
 *
 */
public class EnvironmentMessages extends NLS {
	private static final String BUNDLE_NAME = EnvironmentMessages.class.getName();

	private EnvironmentMessages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, EnvironmentMessages.class);
	}

	public static String Environments_0;

	public static String Environments_1;

	public static String EnvironmentsManager_0;
}
