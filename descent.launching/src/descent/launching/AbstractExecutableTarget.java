package descent.launching;

import java.util.HashSet;
import java.util.Set;

import descent.core.IJavaProject;

/**
 * Base class for an executable target. Although classes requesting a build need
 * only do so using an {@link IExecutableTarget} this class is a basic
 * implementation of an executable target designed to be subclassed/overriden
 * for specific purposes by plugins requesting a build.
 *
 * @author Robert Fraser
 */
public abstract class AbstractExecutableTarget implements IExecutableTarget
{
    protected static final String[] NO_DEFAULTS = new String[] { };
    
    public static final boolean defaultAddDebugInfo           = true;
    public static final boolean defaultAddUnittests           = false;
    public static final boolean defaultAddAssertsAndContracts = true;
    public static final boolean defaultInlineFunctions        = false;
    public static final boolean defaultOptimizeCode           = false;
    public static final boolean defaultInstrumentForCoverage  = false;
    public static final boolean defaultInstrumentForProfile   = false;
    
	private IJavaProject project;
	protected final Set<String> modules = new HashSet<String>();
    
    protected Boolean addDebugInfo;
    protected Boolean addUnittests;
    protected Boolean addAssertsAndContracts;
    protected Boolean inlineFunctions;
    protected Boolean optimizeCode;
    protected Boolean instrumentForCoverage;
    protected Boolean instrumentForProfile;
	
	protected AbstractExecutableTarget()
	{
		String[] defaultModules = getDefaultModules();
		for(String moduleName : defaultModules)
			modules.add(moduleName);
	}
	
    //--------------------------------------------------------------------------
    // Interface implementation
    
	/* (non-Javadoc)
	 * @see descent.launching.IExecutableTarget#getProject()
	 */
	public IJavaProject getProject()
	{
		return project;
	}
    
    /* (non-Javadoc)
     * @see descent.launching.IExecutableTarget#getModules()
     */
    public String[] getModules()
    {
        return modules.toArray(new String[modules.size()]);
    }
    
    /* (non-Javadoc)
     * @see descent.launching.IExecutableTarget#getDefaultImportPath()
     */
    public String[] getDefaultImportPath()
    {
        return NO_DEFAULTS;
    }
    
    /* (non-Javadoc)
     * @see descent.launching.IExecutableTarget#getDefaultDebugIdents()
     */
    public String[] getDefaultDebugIdents()
    {
        return NO_DEFAULTS;
    }

    /* (non-Javadoc)
     * @see descent.launching.IExecutableTarget#getDefaultVersionIdents()
     */
    public String[] getDefaultVersionIdents()
    {
        return NO_DEFAULTS;
    }
	
    /* (non-Javadoc)
     * @see descent.launching.IExecutableTarget#addAssertsAndContracts()
     */
    public boolean getAddAssertsAndContracts()
    {
        return null != addAssertsAndContracts ? 
                addAssertsAndContracts.booleanValue() :
                defaultAddAssertsAndContracts;
    }

    /* (non-Javadoc)
     * @see descent.launching.IExecutableTarget#getAddDebugInfo()
     */
    public boolean getAddDebugInfo()
    {
        return null != addDebugInfo ? 
                addDebugInfo.booleanValue() :
                defaultAddDebugInfo;
    }

    /* (non-Javadoc)
     * @see descent.launching.IExecutableTarget#getAddUnittests()
     */
    public boolean getAddUnittests()
    {
        return null != addUnittests ? 
                addUnittests.booleanValue() :
                defaultAddUnittests;
    }

    /* (non-Javadoc)
     * @see descent.launching.IExecutableTarget#getInlineFunctions()
     */
    public boolean getInlineFunctions()
    {
        return null != inlineFunctions ? 
                inlineFunctions.booleanValue() :
                defaultInlineFunctions;
    }

    /* (non-Javadoc)
     * @see descent.launching.IExecutableTarget#getInstrumentForCoverage()
     */
    public boolean getInstrumentForCoverage()
    {
        return null != instrumentForCoverage ? 
                instrumentForCoverage.booleanValue() :
                defaultInstrumentForCoverage;
    }

    /* (non-Javadoc)
     * @see descent.launching.IExecutableTarget#getInstrumentForProfile()
     */
    public boolean getInstrumentForProfile()
    {
        return null != instrumentForProfile ? 
                instrumentForProfile.booleanValue() :
                defaultInstrumentForProfile;
    }

    /* (non-Javadoc)
     * @see descent.launching.IExecutableTarget#getOptimizeCode()
     */
    public boolean getOptimizeCode()
    {
        return null != optimizeCode ? 
                optimizeCode.booleanValue() :
                defaultOptimizeCode;
    }
    
    //--------------------------------------------------------------------------
    // Setter methods intended to be used by the client
    /**
     * Sets the project. Must be called at least once with a non-null project
     * if {@link #getProject()} is not overriden.
     */
	public void setProject(IJavaProject project)
	{
		this.project = project;
	}
	
    /**
     * Adds the module to the list of modules that should be built
     */
	public void addModule(String moduleName)
	{
		modules.add(moduleName);
	}
	
    /**
     * Removes the module from the list of modules that should be built
     */
	public boolean removeModule(String moduleName)
	{
		return modules.remove(moduleName);
	}
    
    public void setAddAssertsAndContracts(boolean addAssertsAndContracts)
    {
        this.addAssertsAndContracts = Boolean.valueOf(addAssertsAndContracts);
    }

    public void setAddDebugInfo(boolean addDebugInfo)
    {
        this.addDebugInfo = Boolean.valueOf(addDebugInfo);
    }

    public void setAddUnittests(boolean addUnittests)
    {
        this.addUnittests = Boolean.valueOf(addUnittests);
    }

    public void setInlineFunctions(boolean inlineFunctions)
    {
        this.inlineFunctions = Boolean.valueOf(inlineFunctions);
    }

    public void setInstrumentForCoverage(boolean instrumentForCoverage)
    {
        this.instrumentForCoverage = Boolean.valueOf(instrumentForCoverage);
    }

    public void setInstrumentForProfile(boolean instrumentForProfile)
    {
        this.instrumentForProfile = Boolean.valueOf(instrumentForProfile);
    }

    public void setOptimizeCode(boolean optimizeCode)
    {
        this.optimizeCode = Boolean.valueOf(optimizeCode);
    }

    //--------------------------------------------------------------------------
    // Protected methods intended to be overriden by clients if needed
    /**
     * Gets the list of modules that should be added before any others, or the empty
     * list if there is no such list. SHould not return null.
     */
	protected String[] getDefaultModules()
	{
		return NO_DEFAULTS;
	}
}
