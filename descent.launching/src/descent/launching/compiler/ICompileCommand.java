/**
 * 
 */
package descent.launching.compiler;

import java.io.File;
import java.util.List;

/**
 * Represents a D compile command
 * 
 * @author Robert Fraser
 */
public interface ICompileCommand extends IBuildCommand
{
	// Compile only, don't link ("-c" in DMD)
	public boolean getCompileOnly();
	public void setCompileOnly(boolean compileOnly);
	
	// Output directory ("-od" in DMD)
	public File getOutputDirectory();
	public void setOutputDirectory(File outputDirectory);
	
	// Output filename ("-of" in DMD)
	public File getOutputFilename();
	public void setOutputFilename(File outputFilename);
	
	// Paths to search for imports ("-I" in DMD)
	public List<File> getImportPaths();
	public void addImportPath(File importPath);
	
	// Paths to search for import expression files ("-J" in DMD)
	public List<File> getImportExpPaths();
	public void addImportExpPath(File importExpPath);
	
	// Allow deprectaed features ("-d" in DMD)
	public boolean getAllowDeprecated();
	public void setAllowDeprecated(boolean allowDeprecated);
	
	// Show warnings ("-w" in DMD)
	public boolean getShowWarnings();
	public void setShowWarnings(boolean showWarnings);
	
	// Add symbolic debug info ("-g" in DMD)
	public boolean getAddDebugInfo();
	public void setAddDebugInfo(boolean addDebugInfo);
	
	// Add asserts and contracts (if off, "-release" in DMD)
	public boolean getAddAssertsAndContracts();
	public void setAddAssertsAndContracts(boolean addAssertsAndContracts);
	
	// Add unit test code ("-unittest" in DMD)
	public boolean getAddUnittests();
	public void setAddUnittests(boolean addUnittests);
	
	// Insert debugging code ("-debug" in DMD)
	public boolean getInsertDebugCode();
	public void setInsertDebugCode(boolean insertDebugCode);
	
	// Integral debug level, or null if not defined ("-debug=[number]" in DMD)
	public Integer getDebugLevel();
	public void setDebugLevel(Integer debugLevel);
	
	// Debug identifiers ("-debug=[identifier]" in DMD)
	public List<String> getDebugIdents();
	public void addDebugIdent(String debugIdent);
	
	// Integral version level, or null if not defined ("-version=[number]" in DMD)
	public Integer getVersionLevel();
	public void setVersionLevel(Integer versionLevel);
	
	// Version identifiers ("-version=[identifier]" in DMD)
	public List<String> getVersionIdents();
	public void addVersionIdent(String versionIdent);
	
	// Inline functions ("-inline" in DMD)
	public boolean getInlineFunctions();
	public void setInlineFunctions(boolean inlineFunctions);
	
	// Optimize code ("-O" in DMD)
	public boolean getOptimizeCode();
	public void setOptimizeCode(boolean optimizeCode);
	
	// Instrument for coverage analysis ("-cov" in DMD)
	public boolean getInstrumentForCoverage();
	public void setInstrumentForCoverage(boolean instrumentForCoverage);
	
	// Instrument for profiling ("-profile" in DMD)
	public boolean getInstrumentForProfile();
	public void setInstrumentForProfile(boolean instrumentForProfile);
}