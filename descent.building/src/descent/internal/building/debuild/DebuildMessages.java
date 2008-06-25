package descent.internal.building.debuild;

import org.eclipse.osgi.util.NLS;

public class DebuildMessages extends NLS
{
    private static final String BUNDLE_NAME = "descent.internal.building.debuild.DebuildMessages"; //$NON-NLS-1$

    public static String BuilderLaunchDelegate_error_could_not_find_builder;
    public static String BuilderLaunchDelegate_error_could_not_instantiate_builder;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, DebuildMessages.class);
    }

    private DebuildMessages()
    {
    }
}
