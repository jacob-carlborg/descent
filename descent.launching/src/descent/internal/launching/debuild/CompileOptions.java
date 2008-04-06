package descent.internal.launching.debuild;

import java.util.List;

import descent.launching.compiler.ICompileCommand;

/**
 * Tiny public wrapper class for compile options. This only includes options
 * that affect the object code generated (so options like show warnings,
 * allow deprecated, verbose/quiet, etc. are not icnluded in this class).
 * 
 * @author Robert Fraser
 */
/* package */ class CompileOptions
{
	public boolean addDebugInfo;
	public boolean addUnittests;
	public boolean addAssertsAndContracts;
	public boolean insertDebugCode;
	public boolean inlineFunctions;
	public boolean optimizeCode;
	public boolean instrumentForCoverage;
	public boolean instrumentForProfile;
	
	public List<String> debugIdents;
	public List<String> versionIdents;
	public Integer debugLevel;   // Or null if no debug level
	public Integer versionLevel; // Or null if no version level
	
	public void prepareCompileCommand(ICompileCommand cmd)
	{
		cmd.setAddDebugInfo(addDebugInfo);
		cmd.setAddUnittests(addUnittests);
		cmd.setAddAssertsAndContracts(addAssertsAndContracts);
		cmd.setInsertDebugCode(insertDebugCode);
		cmd.setInlineFunctions(inlineFunctions);
		cmd.setOptimizeCode(optimizeCode);
		cmd.setInstrumentForCoverage(instrumentForCoverage);
		cmd.setInstrumentForProfile(instrumentForProfile);
        
        // TODO the levels need not, and in fact, often will not, be specified
        // cmd.setDebugLevel(debugLevel);
        // cmd.setVersionLevel(versionLevel);
        cmd.setDebugIdents(debugIdents);
        cmd.setVersionIdents(versionIdents);
	}
}
