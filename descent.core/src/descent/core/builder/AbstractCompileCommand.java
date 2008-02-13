package descent.core.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCompileCommand extends AbstractBuildCommand 
	implements ICompileCommand
{
	protected boolean            compileOnly;
	protected File               outputDirectory;
	protected File               outputFilename;
	
	protected final List<File>  importPaths = new ArrayList<File>();
	protected final List<File>  importExpPaths = new ArrayList<File>();
	
	protected boolean            allowDeprecated;
	protected boolean            showWarnings;
	protected boolean            addDebugInfo;
	
	protected boolean            addAssertsAndContracts;
	protected boolean            addUnittests;
	
	protected boolean            instrumentForCoverage;
	protected boolean            instrumentForProfile;
	
	protected boolean            insertDebugCode;
	protected Integer            debugLevel;
	protected final List<String> debugIdents = new ArrayList<String>();
	
	protected Integer            versionLevel;
	protected final List<String> versionIdents = new ArrayList<String>();
	
	protected boolean            inlineFunctions;
	protected boolean            optimizeCode;
	
	protected boolean            verbose;
	protected boolean            quiet;
	
	@Override
	public void setDefaults()
	{
		// These should almost always be overriden, so don't worry too much.
		// Just clear the lists & nullify stuff
		compileOnly = true;
		
		outputDirectory = null;
		outputFilename = null;
		
		importPaths.clear();
		importExpPaths.clear();
		
		allowDeprecated = false;
		showWarnings = true;
		addDebugInfo = true;
		
		addAssertsAndContracts = true;
		addUnittests = false;
		
		instrumentForCoverage = false;
		instrumentForProfile = false;
		
		insertDebugCode = true;
		debugLevel = null;
		debugIdents.clear();
		
		versionLevel = null;
		versionIdents.clear();
		
		inlineFunctions = false;
		optimizeCode = false;
		
		verbose = false;
		quiet = false;

		super.setDefaults();
	}
	
	@Override
	public boolean isValid()
	{
		if(null == outputDirectory && null == outputFilename)
			return false;
		if(verbose && quiet)
			return false;
		
		return super.isValid();
	}
	
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getCompileOnly()
	 */
	public boolean getCompileOnly()
	{
		return compileOnly;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setCompileOnly(boolean)
	 */
	public void setCompileOnly(boolean compileOnly)
	{
		this.compileOnly = compileOnly;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getOutputDir()
	 */
	public File getOutputDirectory()
	{
		return outputDirectory;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setOutputDir(java.lang.String)
	 */
	public void setOutputDirectory(File outputDirectory)
	{
		this.outputDirectory = outputDirectory;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getOutputFilename()
	 */
	public File getOutputFilename()
	{
		return outputFilename;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setOutputFilename(java.lang.String)
	 */
	public void setOutputFilename(File outputFilename)
	{
		this.outputFilename = outputFilename;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getImportPaths()
	 */
	public List<File> getImportPaths()
	{
		return importPaths;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setImportPaths(java.util.List)
	 */
	public void addImportPath(File importPath)
	{
		if(!importPaths.contains(importPath))
			importPaths.add(importPath);
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getImportExpPaths()
	 */
	public List<File> getImportExpPaths()
	{
		return importExpPaths;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setImportExpPaths(java.util.List)
	 */
	public void addImportExpPath(File importExpPath)
	{
		if(!importExpPaths.contains(importExpPath))
			importExpPaths.add(importExpPath);
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getAllowDeprecated()
	 */
	public boolean getAllowDeprecated()
	{
		return allowDeprecated;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setAllowDeprecated(boolean)
	 */
	public void setAllowDeprecated(boolean allowDeprecated)
	{
		this.allowDeprecated = allowDeprecated;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getShowWarnings()
	 */
	public boolean getShowWarnings()
	{
		return showWarnings;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setShowWarnings(boolean)
	 */
	public void setShowWarnings(boolean showWarnings)
	{
		this.showWarnings = showWarnings;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getAddDebugInfo()
	 */
	public boolean getAddDebugInfo()
	{
		return addDebugInfo;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setAddDebugInfo(boolean)
	 */
	public void setAddDebugInfo(boolean addDebugInfo)
	{
		this.addDebugInfo = addDebugInfo;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getAddAssertsAndContracts()
	 */
	public boolean getAddAssertsAndContracts()
	{
		return addAssertsAndContracts;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setAddAssertsAndContracts(boolean)
	 */
	public void setAddAssertsAndContracts(boolean addAssertsAndContracts)
	{
		this.addAssertsAndContracts = addAssertsAndContracts;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getAddUnittests()
	 */
	public boolean getAddUnittests()
	{
		return addUnittests;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setAddUnittests(boolean)
	 */
	public void setAddUnittests(boolean addUnittests)
	{
		this.addUnittests = addUnittests;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getInsertDebugCode()
	 */
	public boolean getInsertDebugCode()
	{
		return insertDebugCode;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setInsertDebugCode(boolean)
	 */
	public void setInsertDebugCode(boolean insertDebugCode)
	{
		this.insertDebugCode = insertDebugCode;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getDebugLevel()
	 */
	public Integer getDebugLevel()
	{
		return debugLevel;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setDebugLevel(java.lang.Integer)
	 */
	public void setDebugLevel(Integer debugLevel)
	{
		this.debugLevel = debugLevel;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getDebugIdents()
	 */
	public List<String> getDebugIdents()
	{
		return debugIdents;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setDebugIdents(java.util.List)
	 */
	public void addDebugIdent(String debugIdent)
	{
		if(!debugIdents.contains(debugIdent))
			debugIdents.add(debugIdent);
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getVersionLevel()
	 */
	public Integer getVersionLevel()
	{
		return versionLevel;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setVersionLevel(java.lang.Integer)
	 */
	public void setVersionLevel(Integer versionLevel)
	{
		this.versionLevel = versionLevel;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getVersionIdents()
	 */
	public List<String> getVersionIdents()
	{
		return versionIdents;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setVersionIdents(java.util.List)
	 */
	public void addVersionIdent(String versionIdent)
	{
		if(!versionIdents.contains(versionIdent))
			versionIdents.add(versionIdent);
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getInlineFunctions()
	 */
	public boolean getInlineFunctions()
	{
		return inlineFunctions;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setInlineFunctions(boolean)
	 */
	public void setInlineFunctions(boolean inlineFunctions)
	{
		this.inlineFunctions = inlineFunctions;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getOptimizeCode()
	 */
	public boolean getOptimizeCode()
	{
		return optimizeCode;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setOptimizeCode(boolean)
	 */
	public void setOptimizeCode(boolean optimizeCode)
	{
		this.optimizeCode = optimizeCode;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getInstrumentForCoverage()
	 */
	public boolean getInstrumentForCoverage()
	{
		return instrumentForCoverage;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setInstrumentForCoverage(boolean)
	 */
	public void setInstrumentForCoverage(boolean instrumentForCoverage)
	{
		this.instrumentForCoverage = instrumentForCoverage;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getInstrumentForProfile()
	 */
	public boolean getInstrumentForProfile()
	{
		return instrumentForProfile;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setInstrumentForProfile(boolean)
	 */
	public void setInstrumentForProfile(boolean instrumentForProfile)
	{
		this.instrumentForProfile = instrumentForProfile;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getVerbose()
	 */
	public boolean getVerbose()
	{
		return verbose;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setVerbose(boolean)
	 */
	public void setVerbose(boolean verbose)
	{
		this.verbose = verbose;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#getQuiet()
	 */
	public boolean getQuiet()
	{
		return quiet;
	}
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompileCommand#setQuiet(boolean)
	 */
	public void setQuiet(boolean quiet)
	{
		this.quiet = quiet;
	}
}
