package descent.internal.building;

import java.util.Arrays;

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
    public static final String EXTENSION_DDL;
    public static final String EXTENSION_OBJECT_FILE;
    
    static
    {
        IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");
        
        if(IS_WINDOWS)
        {
            EXTENSION_EXECUTABLE = ".exe";
            EXTENSION_STATIC_LIBRARY = ".lib";
            EXTENSION_DYNAMIC_LIBRARY = ".dll";
            EXTENSION_DDL = ".ddl";
            EXTENSION_OBJECT_FILE = ".obj";
        }
        else
        {
            EXTENSION_EXECUTABLE = "";
            EXTENSION_STATIC_LIBRARY = ".a";
            EXTENSION_DYNAMIC_LIBRARY = ".so";
            EXTENSION_DDL = ".ddl";
            EXTENSION_OBJECT_FILE = ".o";
        }
    }
    
    /**
     * List of all predefined versions in sorted order (according to
     * {@link java.lang.String#compareTo(String)} which means that it can be
     * searched using {@link java.util.Arrays#binarySearch(Object[], Object)}).
     */
    public static final String[] PREDEFINED_VERSIONS = new String[]
    {
        "BigEndian", 
        "D_Coverage", 
        "D_InlineAsm", 
        "D_InlineAsm_X86", 
        "D_Version2", 
        "DigitalMars", 
        "LittleEndian", 
        "Win32", 
        "Win64", 
        "Windows", 
        "X86", 
        "X86_64", 
        "all", 
        "linux", 
        "none", 
        "unittest",
    };
    
    /**
     * Returns true if and only if the given version identifier is a predefined
     * version identifier (case-sensitive). Keywords are also illegal version
     * identifiers, but the compielr should sort that out.
     * 
     * @param id the identifier to check
     * @return   true if id is predefined, false otherwise
     */
    public static final boolean isPredefinedVersion(String id)
    {
        return Arrays.binarySearch(PREDEFINED_VERSIONS, id) >= 0;
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

    public static boolean isValidIdentifier(String id)
    {
        int len = id.length();
        if(0 == len)
            return false;
        
        for(int i = 0; i < len; i++)
            if(!isValidIdChar(id.charAt(i)))
                return false;
        return true;
    }
    
    private static boolean isValidIdChar(char c)
    {
        return 
            (c >= 'a' && c <= 'z') || 
            (c >= 'A' && c <= 'Z') || 
            (c >= '0' && c <= '9') ||
            c == '_' ||
            c >= 128; // Assume anything in unicode is OK
    }
}
