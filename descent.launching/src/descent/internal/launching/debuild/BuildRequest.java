package descent.internal.launching.debuild;

import java.io.File;

import descent.core.IJavaProject;
import descent.internal.launching.dmd.DmdCompilerInterface;
import descent.launching.IExecutableTarget;
import descent.launching.compiler.ICompileCommand;
import descent.launching.compiler.ICompilerInterface;

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
public class BuildRequest
{	
	
	/**
	 * Information about the executable target to be built (is it debug?
	 * should we optimize? Add unit tests? etc., etc.)
	 */
	private final IExecutableTarget target; 
	
	public BuildRequest(IExecutableTarget target)
	{
		this.target = target;
	}
	
	/**
	 * Gets the Java project being built
	 */
	public IJavaProject getProject()
	{
		return target.getProject();
	}
	
	/**
	 * Gets all the modules that must be built for this target
	 */
	public String[] getModules()
	{
		return target.getModules();
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
    
    public CompileOptions getCompileOptions()
    {
        // TODO
        CompileOptions opts = new CompileOptions();
        opts.addDebugInfo = true;
        opts.addUnittests = true;
        opts.addAssertsAndContracts = true;
        opts.insertDebugCode = true;
        opts.inlineFunctions = false;
        opts.optimizeCode = false;
        opts.instrumentForCoverage = false;
        opts.instrumentForProfile = false;
        return opts;
    }
    
    public ICompileCommand getCompileCommand()
    {
        ICompileCommand cmd = getCompilerInterface().createCompileCommand();
        
        // TODO
        cmd.setExecutableFile(new File("C:\\dmd\\bin\\dmd.exe"));
        cmd.setShowWarnings(true);
        cmd.setAllowDeprecated(false);
        cmd.setVerbose(false);
        cmd.setQuiet(true);
        
        return cmd;
    }
	
    public ICompilerInterface getCompilerInterface()
    {
        // TODO
        return DmdCompilerInterface.getInstance();
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
            //TODO uncomment "std.",
	    };
        
        tangoIgnored = new String[]
        {
            "object",
            "gcc.",
        };
	}
}
