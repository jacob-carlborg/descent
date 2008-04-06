package descent.internal.launching.debuild;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;

import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.launching.IExecutableTarget;
import descent.launching.IVMInstall;
import descent.launching.JavaRuntime;
import descent.launching.compiler.ICompileCommand;
import descent.launching.compiler.ICompilerInterface;
import descent.launching.compiler.ILinkCommand;

/**
 * Wrapper for information about a build request. Exactly one object
 * of this type will exist per DebuildBuilder, and this object should
 * generally simply serve as a wrapper for abstracting getting information
 * that the build needs from the {@link IExecutableTarget}.
 * 
 * Note that many of these methods are simply wrappers for 
 * <code>IExecutableTarget</code> methods. This is okay, since it helps abstract
 * these things from the builder if that interface ever changes.
 *
 * @author Robert Fraser
 */
/* package */ class BuildRequest
{
	/**
	 * Information about the executable target to be built (is it debug?
	 * should we optimize? Add unit tests? etc., etc.)
	 */
	private final IExecutableTarget target;
    private final IJavaProject project;
    private final IVMInstall compilerType;
	
	public BuildRequest(IExecutableTarget target)
	{
		this.target = target;
        this.project = target.getProject();
        
        IVMInstall compilerType = null;
        try
        {
            compilerType = JavaRuntime.getVMInstall(project);
        }
        catch(CoreException e)
        {
            // compilerType remains null...
        }
        
        if(null == compilerType)
        {
            compilerType = JavaRuntime.getDefaultVMInstall();
            if(null == compilerType)
                throw new DebuildException("No compiler has been defined for this project");
        }
        
        this.compilerType = compilerType;
	}
	
	/**
	 * Gets the Java project being built
	 */
	public IJavaProject getProject()
	{
		return project;
	}
	
	/**
	 * Gets all the modules that must be built for this target
	 */
	public String[] getModules()
	{
		return target.getModules();
	}
    
    /**
     * Gets the default include path
     */
    public String[] getDefaultImportPath()
    {
        return target.getDefaultImportPath();
    }
	
	/**
	 * Gets the list of modules that should be ignored for this build.
	 * Generally, this is standard-library dependant. The ignore list
	 * should be an array of strings of modules to ignore. If anything
	 * in a package or all subpackages should be ignored, end it with
	 * a ".".
	 */
	public String[] getIgnoreList()
	{
		//TODO
		return phobosIgnored;
	}
    
    /**
     * Gets the compile options that affect the generated object code.
     */
    public CompileOptions getCompileOptions()
    {
        CompileOptions opts = new CompileOptions();
        
        // Set the executable-target-specific options
        opts.addDebugInfo = target.getAddDebugInfo();
        opts.addUnittests = target.getAddUnittests();
        opts.addAssertsAndContracts = target.getAddAssertsAndContracts();
        opts.inlineFunctions = target.getInlineFunctions();
        opts.optimizeCode = target.getOptimizeCode();
        opts.instrumentForCoverage = target.getInstrumentForCoverage();
        opts.instrumentForProfile = target.getInstrumentForProfile();
        for(String ident : target.getDefaultVersionIdents())
            opts.debugIdents.add(ident);
        for(String ident : target.getDefaultDebugIdents())
            opts.debugIdents.add(ident);
        
        // Set the project-specific options
        opts.insertDebugCode = true; // WAITING_ON_CORE
        opts.versionLevel = getVersionLevel();
        opts.debugLevel = getDebugLevel();
        opts.versionIdents = getVersionIdents();
        opts.debugIdents = getDebugIdents();
        
        return opts;
    }
    
    /**
     * Gets a new compile command with request-sepcific defaults set for options
     * that don't affect code generation (that is, will warnings be shown? will
     * deprecaated features be allowed? etc.). Also sets the compiler executable
     * path. Other options (such as the import path and code generation options)
     * must be set elsewhere. These options may be overriden, of course.
     */
    public ICompileCommand getCompileCommand()
    {
        ICompileCommand cmd = getCompilerInterface().createCompileCommand();
        
        cmd.setExecutableFile(compilerType.getBinaryLocation());
        cmd.setShowWarnings(false);
        cmd.setAllowDeprecated(true);
        
        return cmd;
    }
    
    public ILinkCommand getLinkCommand()
    {
        ILinkCommand cmd = getCompilerInterface().createLinkCommand();
        
        cmd.setExecutableFile(compilerType.getBinaryLocation());
        
        return cmd;
    }
	
    public ICompilerInterface getCompilerInterface()
    {
        return compilerType.getCompilerInterface();
    }
    
    //--------------------------------------------------------------------------
    // CACHE MANAGEMENT
    //--------------------------------------------------------------------------
    class Level
    {
        Integer value;
    }
    
    // Cached information
    private Level debugLevel;
    private Integer getDebugLevel()
    {
        if(null == debugLevel)
        {
            debugLevel = new Level();
            debugLevel.value = getLevel(JavaCore.COMPILER_DEBUG_LEVEL);
        }
        return debugLevel.value;
    }
    
    private Level versionLevel;
    private Integer getVersionLevel()
    {
        if(null == versionLevel)
        {
            versionLevel = new Level();
            versionLevel.value = getLevel(JavaCore.COMPILER_VERSION_LEVEL);
        }
        return versionLevel.value;
    }
    
    private List<String> debugIdents;
    private List<String> getDebugIdents()
    {
        if(null == debugIdents)
        {
            debugIdents = getIdentifiers(JavaCore.COMPILER_DEBUG_IDENTIFIERS, false);
        }
        return debugIdents;
    }
    
    private List<String> versionIdents;
    private List<String> getVersionIdents()
    {
        if(null == versionIdents)
        {
            versionIdents = getIdentifiers(JavaCore.COMPILER_DEBUG_IDENTIFIERS, true);
        }
        return versionIdents;
    }
    
    private Integer getLevel(String preference)
    {
        String level = project.getOption(preference, true);
        if(null == level || "" == level)
            return null;
        
        try
        {
            return new Integer(level);
        }
        catch(NumberFormatException e)
        {
            return null;
        }
    }
    
    private List<String> getIdentifiers(String preference, boolean removePredefined)
    {
        String[] option = project.getOption(preference, true).split(",");
        List<String> idents = new ArrayList<String>();
        csv: for(String val : option)
        {
            val = val.trim();
            if(removePredefined && predefinedVersions.contains(val))
                continue csv;
            if(val.equals(""))
                continue csv;
            for(int i = 0; i < val.length(); i++)
            {
                char c = val.charAt(i);
                if(!(
                    (c >= 'A' && c <= 'Z') ||
                    (c >= 'a' && c <= 'z') ||
                    (c >= '0' && c <= '9') ||
                    (c == '_')
                ))
                    continue csv;
            }
            idents.add(val);
        }
        return idents;
    }
    
    //--------------------------------------------------------------------------
    // CONSTANTS
    // PERHAPS these shouldn't be hardcoded
    //--------------------------------------------------------------------------
    
    // Ignored modules
	private static final String[] phobosIgnored = new String[]
    {
        "object",
        "crc32",
        "gcc.",
        "gcstats",
        "std.",
    };
	private static final String[] tangoIgnored = new String[]
    {
        "object",
        "gcc.",
    };
    
    // Predefined identifiers
    private static final HashSet<String> predefinedVersions = new HashSet<String>();
    
    static
    {
        predefinedVersions.add("DigitalMars");
        predefinedVersions.add("X86");
        predefinedVersions.add("X86_64");
        predefinedVersions.add("Windows");
        predefinedVersions.add("Win32");
        predefinedVersions.add("Win64");
        predefinedVersions.add("linux");
        predefinedVersions.add("LittleEndian");
        predefinedVersions.add("BigEndian");
        predefinedVersions.add("D_Coverage");
        predefinedVersions.add("D_InlineAsm");
        predefinedVersions.add("D_InlineAsm_X86");
        predefinedVersions.add("D_Version2");
        predefinedVersions.add("none");
        predefinedVersions.add("all");
    };
}
