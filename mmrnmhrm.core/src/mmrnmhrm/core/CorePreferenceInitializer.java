package mmrnmhrm.core;



import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class CorePreferenceInitializer extends AbstractPreferenceInitializer {

	public static final String PREFIX = DeeCore.PLUGIN_ID + "."; 

	public static final String REPORT_SYNTAX_ERRORS = PREFIX + "report_syntax_errors";
	
	public CorePreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {

	 	IEclipsePreferences defaultPreferences = (new DefaultScope()).getNode(DeeCore.PLUGIN_ID);
	 	defaultPreferences.putBoolean(REPORT_SYNTAX_ERRORS, true); 
	 	
	 	//IEclipsePreferences instancePreferences = getPreferences();
	 	//instancePreferences.putBoolean(REPORT_SYNTAX_ERRORS, true); 
	}


	
/*	private static IPreferenceStore preferenceStore = null;

	public static IPreferenceStore getPreferenceStore() {
        // Create the preference store lazily.
        if (preferenceStore == null) {
            preferenceStore = new ScopedPreferenceStore(new InstanceScope(), DeeCore.getInstance().getBundle().getSymbolicName());

        }
        return preferenceStore;
    }
*/
	
}
