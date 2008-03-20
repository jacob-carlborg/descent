package descent.internal.launching.debuild;

import java.io.File;

import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.internal.launching.dmd.DmdCompilerInterface;
import descent.launching.IExecutableTarget;
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
	
	public BuildRequest(IExecutableTarget target)
	{
		this.target = target;
        this.project = target.getProject();
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
        opts.versionLevel = getLevel(JavaCore.COMPILER_VERSION_LEVEL);
        opts.debugLevel = getLevel(JavaCore.COMPILER_DEBUG_LEVEL);
        for(String ident : getIdentifiers(JavaCore.COMPILER_VERSION_IDENTIFIERS))
            opts.versionIdents.add(ident);
        for(String ident : getIdentifiers(JavaCore.COMPILER_DEBUG_IDENTIFIERS))
            opts.debugIdents.add(ident);
        
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
        
        // TODO
        cmd.setExecutableFile(new File("C:\\d\\dmd\\bin\\dmd.exe"));
        cmd.setShowWarnings(false);
        cmd.setAllowDeprecated(true);
        
        return cmd;
    }
    
    public ILinkCommand getLinkCommand()
    {
        ILinkCommand cmd = getCompilerInterface().createLinkCommand();
        
        cmd.setExecutableFile(new File("C:\\d\\dmd\\bin\\dmd.exe"));
        
        return cmd;
    }
	
    public ICompilerInterface getCompilerInterface()
    {
        // TODO
        return DmdCompilerInterface.getInstance();
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
    
    private String[] getIdentifiers(String preference)
    {
        return project.getOption(preference, true).split(",");
    }
    
	// PERHAPS this shouldn't be hardcoded
	private static final String[] phobosIgnored;
	private static final String[] tangoIgnored;
	static
	{
	    phobosIgnored = new String[]
	    {
            "object",
	        "crc32",
            "gcc.",
            "gcstats",
            "std.",
	    };
        
        tangoIgnored = new String[]
        {
            "object",
            "gcc.",
        };
	}
}
