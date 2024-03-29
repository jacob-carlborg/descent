package descent.internal.building;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.building.CompilerInterfaceRegistry;
import descent.building.ICompilerInterfaceType;
import descent.building.compiler.ICompilerInterface;
import descent.core.IJavaProject;
import descent.launching.IVMInstall;
import descent.launching.JavaRuntime;

/**
 * Static utility methods used throughout the builder. THis class is not part of
 * the debuild package, as it is designed to be used in the UI as well.
 * 
 * @author Robert Fraser
 */
@SuppressWarnings("unchecked")
public class BuilderUtil
{
    //--------------------------------------------------------------------------
    // OS-Specific functionality
    
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
        // This needs to be initialized here to prevent constant folding
        IS_WINDOWS = System.getProperty("os.name").startsWith("Windows"); //$NON-NLS-1$ //$NON-NLS-2$
        
        if(IS_WINDOWS)
        {
            EXTENSION_EXECUTABLE = ".exe"; //$NON-NLS-1$
            EXTENSION_STATIC_LIBRARY = ".lib"; //$NON-NLS-1$
            EXTENSION_DYNAMIC_LIBRARY = ".dll"; //$NON-NLS-1$
            EXTENSION_DDL = ".ddl"; //$NON-NLS-1$
            EXTENSION_OBJECT_FILE = ".obj"; //$NON-NLS-1$
        }
        else
        {
            EXTENSION_EXECUTABLE = ""; //$NON-NLS-1$
            EXTENSION_STATIC_LIBRARY = ".a"; //$NON-NLS-1$
            EXTENSION_DYNAMIC_LIBRARY = ".so"; //$NON-NLS-1$
            EXTENSION_DDL = ".ddl"; //$NON-NLS-1$
            EXTENSION_OBJECT_FILE = ".o"; //$NON-NLS-1$
        }
    }
    
    //--------------------------------------------------------------------------
    // Version/debug identifier management
    
    /**
     * List of all predefined versions in sorted order (according to
     * {@link java.lang.String#compareTo(String)} which means that it can be
     * searched using {@link java.util.Arrays#binarySearch(Object[], Object)}).
     */
    public static final String[] PREDEFINED_VERSIONS = new String[]
    {
        "BigEndian",  //$NON-NLS-1$
        "D_Coverage",  //$NON-NLS-1$
        "D_InlineAsm",  //$NON-NLS-1$
        "D_InlineAsm_X86",  //$NON-NLS-1$
        "D_Version2",  //$NON-NLS-1$
        "DigitalMars",  //$NON-NLS-1$
        "LittleEndian",  //$NON-NLS-1$
        "Win32",  //$NON-NLS-1$
        "Win64",  //$NON-NLS-1$
        "Windows",  //$NON-NLS-1$
        "X86",  //$NON-NLS-1$
        "X86_64",  //$NON-NLS-1$
        "all",  //$NON-NLS-1$
        "linux",  //$NON-NLS-1$
        "none",  //$NON-NLS-1$
        "unittest", //$NON-NLS-1$
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
     * Checks whether the given string is a valid D identifier. Valid D
     * identifiers are the same as valid Java identifiers with the exception
     * that Java allows '$'.
     * 
     * @param id
     * @return
     */
    public static boolean isValidIdentifier(String id)
    {
        int len = id.length();
        if(0 == len)
            return false;
        
        if(!isValidIdStart(id.charAt(0)))
            return false;
        for(int i = 1; i < len; i++)
            if(!isValidIdPart(id.charAt(i)))
                return false;
        return true;
    }
    
    private static boolean isValidIdStart(char c)
    {
        return Character.isJavaIdentifierStart(c) && !(c == '$');
    }
    
    private static boolean isValidIdPart(char c)
    {
        return Character.isJavaIdentifierPart(c) && !(c == '$');
    }
    
    //--------------------------------------------------------------------------
    // Compiler management
    
    private static final CompilerInterfaceRegistry registry = 
        CompilerInterfaceRegistry.getInstance();
    
    /**
     * Gets the compiler interface for the given compiler.
     * 
     * @param compiler the compiler to get the interface for
     * @return         the interface to the compiler or null if not such compiler
     *                 interface could be found
     */
    public static final ICompilerInterface getCompilerInterface(IVMInstall compiler)
    {
        if(null == compiler)
            return null;
        
        try
        {
            ICompilerInterfaceType type = registry.
                    getCompilerInterfaceByVMInstallType(compiler.
                    getVMInstallType());
            return null != type ? type.getCompilerInterface() : null;
        }
        catch(CoreException e)
        {
            BuildingPlugin.log(e);
            return null;
        }
    }
    
    /**
     * Gets the VMInstall (compiler) associated with the given project
     * 
     * @param project the project to get the compiler for
     * @return        the compiler associated with the project or null if either
     *                poject was null or no compielr is associated with project
     */
    public static IVMInstall getVMInstall(IJavaProject project)
        throws CoreException
    {
        if(null == project)
            return null;
        return JavaRuntime.getVMInstall(project);
    }
    
    //--------------------------------------------------------------------------
    // Wrappers for ILaunchConfiguration methods which hide the exceptions,
    // since the exception will never be thrown
    
    public static String getAttribute(ILaunchConfiguration config, String id,
            String defaultValue)
    {
        String value = defaultValue;
        try
        {
            value = config.getAttribute(id, defaultValue);
        }
        catch(CoreException e) { }
        return value;
    }
    
    public static boolean getAttribute(ILaunchConfiguration config, String id,
            boolean defaultValue)
    {
        boolean value = defaultValue;
        try
        {
            value = config.getAttribute(id, defaultValue);
        }
        catch(CoreException e) { }
        return value;
    }
    
    public static int getAttribute(ILaunchConfiguration config, String id,
            int defaultValue)
    {
        int value = defaultValue;
        try
        {
            value = config.getAttribute(id, defaultValue);
        }
        catch(CoreException e) { }
        return value;
    }
    
    public static List getAttribute(ILaunchConfiguration config, String id,
            List defaultValue)
    {
        List value = defaultValue;
        try
        {
            value = config.getAttribute(id, defaultValue);
        }
        catch(CoreException e) { }
        return value;
    }
    
    public static Map getAttribute(ILaunchConfiguration config, String id,
            Map defaultValue)
    {
        Map value = defaultValue;
        try
        {
            value = config.getAttribute(id, defaultValue);
        }
        catch(CoreException e) { }
        return value;
    }
    
    public static Set getAttribute(ILaunchConfiguration config, String id,
            Set defaultValue)
    {
        Set value = defaultValue;
        try
        {
            value = config.getAttribute(id, defaultValue);
        }
        catch(CoreException e) { }
        return value;
    }
    
    //--------------------------------------------------------------------------
    // Miscellaneous
    
    /**
     * A monitor for tasks that do not require 
     * 
     * Don't use this as the main null progress monitor, since its 
     * canceleld state is not garunteed.
     */
    public static final IProgressMonitor NO_MONITOR = new NullProgressMonitor();
    
    /**
     * An empty list to use as a default for list-typed constants (there's no
     * similar constant here for the empty string since the empty string is
     * internalized by the JVM).
     */
    public static final List EMPTY_LIST = new ArrayList(0);
    
    /**
     * An empty array object to be used by content providers for elements
     * that are barren and childless.
     */
    public static final Object[] EMPTY_ARRAY = new Object[] {};
    
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
