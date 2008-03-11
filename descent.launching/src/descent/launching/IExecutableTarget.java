package descent.launching;

import descent.core.IJavaProject;

/**
 * Interface for providing information about a requested compile. The way the
 * Descent builder works is that it doesn't actually do any building of binary
 * files during the standard Eclipse build cycle. Instead, it waits for a run
 * to be invoked that depends on an executable, then builds that executable then
 * (if it's not built already).
 * 
 * This class provides a means for specifying what sort of build you want to
 * invoke. Generally, each launch configuration for a D project will need a
 * different <code>IExecutableTarget</code> implementation that specifies
 * information specific to that launch type.
 *
 * @author Robert Fraser
 */
public interface IExecutableTarget
{
	/**
	 * Gets the project currently being built.
	 */
	public IJavaProject getProject();
	
	/**
	 * Gets the list of compilation units that must be built into this project. The set should
	 * not be modified after a call to this method has been made
	 */
	public String[] getModules();
    
    /**
     * Gets the list of additional paths (outside the project classpath) that should
     * be added the the import path. This exists to support executable targets which
     * require bootstrapping code (in particular, Flute). Must return non-null, but
     * should return an empty array if no new paths need to be added to the import
     * path.
     */
    public String[] getDefaultImportPath();
    
    /**
     * Gets the list of additional debug identifiers (outside of the project
     * configuration) that should be added while compiling. This is build-wide
     * (rather than specific to each file). Must return non-null, but should
     * return an empty array if no new debug identifiers need to be added.
     */
    public String[] getDefaultDebugIdents();
    
    /**
     * Gets the list of additional version identifiers (outside of the project
     * configuration) that should be added while compiling. This is build-wide
     * (rather than specific to each file). Must return non-null, but should
     * return an empty array if no new version identifiers need to be added.
     */
    public String[] getDefaultVersionIdents();
    
    /**
     * Returns true if and only if symbolic debug info should be compiled in
     * (i.e. the "-g" switch should be used with the compiler).
     */
    public boolean getAddDebugInfo();
    
    /**
     * Returns true if and only if unit tests should be compiled in (i.e. the
     * "-unittest" switch should be used with the compiler). The builder will throw
     * an exception if the compiler being used does not support this.
     */
    public boolean getAddUnittests();
    
    /**
     * Returns true if and only if asserts and contracts should be added to the
     * compiled binary (i.e. the compiler's "-release" switch should OFF). Will be
     * silently ignored if the compiler being used does not support this.
     */
    public boolean getAddAssertsAndContracts();
    
    /**
     * Returns true if and only if functions should be inlined. Will be silently
     * ignored if the compiler being used does not support this. 
     */
    public boolean getInlineFunctions();
    
    /**
     * Returns true if and only if the compiler should be asked to optimize code.
     * The builder will be silently ignored if the compiler being used does not 
     * support this.
     */
    public boolean getOptimizeCode();
    
    /**
     * Returns true if and only if the compiler should instrument the output for
     * code coverage analysis. The builder will throw an exception if the compiler
     * being used does not support this. 
     * 
     * Note: AFAIK, this is only supported by DMD, so you better be damn sure the
     * compiler works with this before returning "true" from here.
     */
    public boolean getInstrumentForCoverage();
    
    /**
     * Returns true if and only if the compiler should instrument the output for
     * code profiling. The builder will throw an exception if the compiler
     * being used does not support this.
     */
    public boolean getInstrumentForProfile();
}
