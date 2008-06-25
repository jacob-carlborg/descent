package descent.internal.building;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class BuildingPlugin extends AbstractUIPlugin
{   
	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "descent.building"; //$NON-NLS-1$
	
	/**
     * Identifier for 'dBuilders' extension point
     */
    public static final String ID_EXTENSION_POINT_D_BUILDERS = "dBuilders"; //$NON-NLS-1$
    
    /**
     * Identifier for the 'compilerInterfaces' extension point
     */
    public static final String ID_EXTENSION_POINT_COMPILER_INTERFACES = "compilerInterfaces"; //$NON-NLS-1$
    
    /**
     * The build group identifier
     */
    public static final String ID_BUILD_GROUP = "descent.building.builders"; //$NON-NLS-1$
    
	/**
	 * The shared instance
	 */
	private static BuildingPlugin plugin;
	
	/**
	 * The constructor
	 */
	public BuildingPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static BuildingPlugin getDefault()
	{
		return plugin;
	}
	
	private static final IPath ICONS_PATH = new Path("$nl$/icons/full"); //$NON-NLS-1$
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param relativePath the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String relativePath)
	{
	    IPath path = ICONS_PATH.append(relativePath); 
        return createImageDescriptor(getDefault().getBundle(), path, true);
	}
	
	/**
     * Creates an image descriptor for the given path in a bundle. The path can
     * contain variables like $NL$. If no image could be found,
     * <code>useMissingImageDescriptor</code> decides if either the 'missing
     * image descriptor' is returned or <code>null</code>.
     * 
     * @param bundle
     * @param path
     * @param useMissingImageDescriptor
     * @return an {@link ImageDescriptor}, or <code>null</code> iff there's
     *         no image at the given location and
     *         <code>useMissingImageDescriptor</code> is <code>true</code>
     */
    private static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path, boolean useMissingImageDescriptor)
    {
        URL url= FileLocator.find(bundle, path, null);
        if (url != null) {
            return ImageDescriptor.createFromURL(url);
        }
        if (useMissingImageDescriptor) {
            return ImageDescriptor.getMissingImageDescriptor();
        }
        return null;
    }
    
    /**
     * Returns a section in the Java plugin's dialog settings. If the section doesn't exist yet, it is created.
     *
     * @param name the name of the section
     * @return the section of the given name
     */
    public IDialogSettings getDialogSettingsSection(String name) {
        IDialogSettings dialogSettings= getDialogSettings();
        IDialogSettings section= dialogSettings.getSection(name);
        if (section == null) {
            section= dialogSettings.addNewSection(name);
        }
        return section;
    }
	
	public static String getUniqueIdentifier()
	{
        return PLUGIN_ID;
    }
	
	public static void log(IStatus status)
	{
        getDefault().getLog().log(status);
    }
    
    public static void log(String message)
    {
        log(new Status(IStatus.ERROR, getUniqueIdentifier(), IStatus.ERROR, message, null));
    }   
        
    public static void log(Throwable e)
    {
        log(new Status(IStatus.ERROR, getUniqueIdentifier(), IStatus.ERROR, e.getMessage(), e));
    }
}
