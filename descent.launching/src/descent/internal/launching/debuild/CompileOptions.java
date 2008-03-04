package descent.internal.launching.debuild;

import java.util.ArrayList;
import java.util.List;

import descent.launching.compiler.ICompileCommand;

/**
 * Tiny public wrapper class for compile options.
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
	
	public List<String> debugIdents = new ArrayList<String>();
	public List<String> versionIdents = new ArrayList<String>();
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
	}
}
