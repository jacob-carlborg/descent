package descent.internal.building.debuild;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**
 * Static utility methods used throughout the builder
 * 
 * @author Robert Fraser
 */
/* package */ class Util
{
    private static final boolean IS_WINDOWS = 
        System.getProperty("os.name").startsWith("Windows");
    
    /**
     * Gets the absolute OS path for the given Eclipse path (with portable
     * separarators, etc.).
     * 
     * @param path
     * @return
     */
    public static String getAbsolutePath(IPath path)
    {
        IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
        if(null != res)
            path = res.getLocation();
        
        return path.toPortableString();
    }
    
    /**
     * Returns true if and only if the current OS is Windows
     */
    public static boolean isWindows()
    {
        return IS_WINDOWS;
    }
}
