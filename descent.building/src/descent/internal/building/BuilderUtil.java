package descent.internal.building;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**
 * Static utility methods used throughout the builder
 * 
 * @author Robert Fraser
 */
public class BuilderUtil
{
    /**
     * True if the current OS is Windows-based, false otherwise
     */
    public static final boolean IS_WINDOWS;
    
    public static final String EXTENSION_EXECUTABLE;
    public static final String EXTENSION_STATIC_LIBRARY;
    public static final String EXTENSION_DYNAMIC_LIBRARY;
    public static final String EXTENSION_OBJECT_FILE;
    
    static
    {
        IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");
        
        if(IS_WINDOWS)
        {
            EXTENSION_EXECUTABLE = ".exe";
            EXTENSION_STATIC_LIBRARY = ".lib";
            EXTENSION_DYNAMIC_LIBRARY = ".dll";
            EXTENSION_OBJECT_FILE = ".obj";
        }
        else
        {
            EXTENSION_EXECUTABLE = "";
            EXTENSION_STATIC_LIBRARY = ".a";
            EXTENSION_DYNAMIC_LIBRARY = ".so";
            EXTENSION_OBJECT_FILE = ".o";
        }
    }
    
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
        
        return path.toString();
    }
}
