package descent.internal.launching.debuild;

import java.util.ArrayList;
import java.util.List;

import descent.core.IJavaProject;
import descent.launching.compiler.ICompileCommand;

public class ObjectFile extends AbstractBinaryFile
{
	public static final char COMPONENT_SEPARATOR      = '_';
	public static final char PACKAGE_SEPARATOR        = '-';
	public static final char SYMBOLIC_DEBUG           = 'g';
	public static final char UNITTEST                 = 'u';
	public static final char ASSERTS_AND_CONTRACTS    = 'r';
	public static final char ADD_DEBUG_CODE           = 'd';
	public static final char INLINE_FUNCTIONS         = 'i';
	public static final char OPTIMIZE_CODE            = 'o';
	public static final char INSTRUMENT_FOR_COVERAGE  = 'c';
	public static final char INSTRUMENT_FOR_PROFILE   = 'p';
	
	public String moduleName;
	
	public boolean addDebugInfo;
	public boolean addUnittests;
	public boolean addAssertsAndContracts;
	public boolean insertDebugCode;
	public boolean inlineFunctions;
	public boolean optimizeCode;
	public boolean instrumentForCoverage;
	public boolean instrumentForProfile;
	
	public final List<String> debugIdents = new ArrayList<String>();
	public final List<String> versionIdents = new ArrayList<String>();
	public Integer debugLevel;   // Or null if no debug level
	public Integer versionLevel; // Or null if no version level
	
	public ObjectFile(IJavaProject proj)
	{
		super(proj);
		setDefaults();
	}
	
	@Override
	public boolean isValid()
	{
		if(!super.isValid())
			return false;
		
		return moduleName != null;
	}
	
	@Override
	public String getFilename()
	{
		return getMangledName() + "." + getExtension();
	}
	
	/**
	 * Sets the options on the command to match the options in this file. Will
	 * not clear existing debug and version idents, if any have been added to
	 * the command, and will not change any of the command's options not
	 * covered by the scope of this class. The options it will set are:
	 *     - addDebugInfo
	 *     - addUnittests
	 *     - addAssertsAndContracts
	 *     - insertDebugCode
	 *     - inlineFunctions
	 *     - optimizeCode
	 *     - instrumentForCoverage
	 *     - instrumentForProfile
	 *     - versionLevel
	 *     - versionIdents
	 *     - debugLevel
	 *     - debugIdents
	 * 
	 * @param cmd the command to set the options for
	 */
	public void setCompileCommandOptions(ICompileCommand cmd)
	{
		cmd.setAddDebugInfo(addDebugInfo);
		cmd.setAddUnittests(addUnittests);
		cmd.setAddAssertsAndContracts(addAssertsAndContracts);
		cmd.setInsertDebugCode(insertDebugCode);
		cmd.setInlineFunctions(inlineFunctions);
		cmd.setOptimizeCode(optimizeCode);
		cmd.setInstrumentForCoverage(instrumentForCoverage);
		cmd.setInstrumentForProfile(instrumentForProfile);
		
		cmd.setVersionLevel(versionLevel);
		for(String versionIdent : versionIdents)
			cmd.addVersionIdent(versionIdent);
		
		cmd.setDebugLevel(debugLevel);
		for(String debugIdent : debugIdents)
			cmd.addVersionIdent(debugIdent);
	}
	
	private void setDefaults()
	{
		addDebugInfo = true;
		addUnittests = false;
		addAssertsAndContracts = true;
		inlineFunctions = false;
		optimizeCode = false;
		instrumentForCoverage = false;
		instrumentForProfile = false;
	}
	
	private String getMangledName()
	{
		StringBuffer buf = new StringBuffer();
		
		if(addDebugInfo)           buf.append(SYMBOLIC_DEBUG);
		if(addUnittests)           buf.append(UNITTEST);
		if(addAssertsAndContracts) buf.append(ASSERTS_AND_CONTRACTS);
		if(insertDebugCode)        buf.append(ADD_DEBUG_CODE);
		if(inlineFunctions)        buf.append(INLINE_FUNCTIONS);
		if(optimizeCode)           buf.append(OPTIMIZE_CODE);
		if(instrumentForCoverage)  buf.append(INSTRUMENT_FOR_COVERAGE);
		if(instrumentForProfile)   buf.append(INSTRUMENT_FOR_PROFILE);
		
		buf.append(COMPONENT_SEPARATOR);
		buf.append(hashInfo(versionIdents, versionLevel));
		buf.append(COMPONENT_SEPARATOR);
		buf.append(hashInfo(debugIdents, debugLevel));
		buf.append(COMPONENT_SEPARATOR);
		buf.append(moduleName.replace('.', PACKAGE_SEPARATOR));
		
		return buf.toString();
	}
	
	private static String hashInfo(List<String> idents, Integer level)
	{
		// PERHAPS this hash function isn't great. if I have the time, i'd
		// love to go through & fix it (figure out the relations, etc.) Also,
		// there can be hash collisions (of course) which can really screw
		// things up.
		int result = null != level ? level.intValue() * 3001 : 0;
		for(String ident : idents)
		{
			// Note: this can't be order-dependant, since the order of version
			// and debug identifiers doesn't matter in the compile
			result += ident.hashCode() * 31;
		}
		return base64Encode(result);
	}
	
	private static final char[] BASE_64_CHARS = 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+="
		.toCharArray();
	private static String base64Encode(int val)
	{
		// Note: this is not any RFC-compiant base64 encoding. Instead, this is
		// a base64 encoding that attempts to be filesystem-neutral. MIME base64
		// uses the / character, which is not allowed on certain filesystems. It
		// does not use placeholders (= is used for that purpose in the MIME
		// stanard, = replaces / in this encoding).
		StringBuffer result = new StringBuffer();
		while(val != 0)
		{
			result.append(BASE_64_CHARS[val & 63]);
			val >>>= 6;
		}
		return result.toString();
	}
	
	private String getExtension()
	{
		// TODO
		return "obj";
	}
}
