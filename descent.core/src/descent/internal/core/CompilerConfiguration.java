package descent.internal.core;

import descent.core.JavaCore;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.core.util.Util;

public class CompilerConfiguration {
	
	// 0: no, 1: some, 2: full
	public int semanticAnalysisLevel;
	public long versionLevel;
	public HashtableOfCharArrayAndObject versionIdentifiers;
	public long debugLevel;
	public HashtableOfCharArrayAndObject debugIdentifiers;
	public boolean useDeprecated;
	public boolean warnings;
	
	public CompilerConfiguration() {
		semanticAnalysisLevel = (int) getLevel(JavaCore.COMPILER_SHOW_SEMANTIC_ERRORS);
		
		debugLevel = getLevel(JavaCore.COMPILER_DEBUG_LEVEL);
		debugIdentifiers = getIdentifiers(JavaCore.COMPILER_DEBUG_IDENTIFIERS);
		
		versionLevel = getLevel(JavaCore.COMPILER_DEBUG_LEVEL);
		versionIdentifiers = getIdentifiers(JavaCore.COMPILER_VERSION_IDENTIFIERS);
		
		useDeprecated = JavaCore.getOption(JavaCore.COMPILER_ALLOW_DEPRECATED).equals(JavaCore.ENABLED);
		warnings = JavaCore.getOption(JavaCore.COMPILER_ENABLE_WARNINGS).equals(JavaCore.ENABLED);
	}
	
	public boolean isVersionEnabled(char[] version) {
		return versionIdentifiers.containsKey(version);
	}
	
	public boolean isVersionEnabled(long version) {
		return versionLevel >= version;
	}
	
	public boolean isDebugEnabled(char[] version) {
		return debugIdentifiers.containsKey(version);
	}
	
	public boolean isDebugEnabled(long version) {
		return debugLevel >= version;
	}
	
	private static long getLevel(String prefKey) {
		String Level = JavaCore.getOption(prefKey);
		try {
			return Long.parseLong(Level);
		} catch (NumberFormatException e) {
			Util.log(e);
			return 0;
		}
	}
	
	private HashtableOfCharArrayAndObject getIdentifiers(String pref) {
		HashtableOfCharArrayAndObject hash = new HashtableOfCharArrayAndObject();
		
		String prefValue = JavaCore.getOption(pref);
		String[] idents = prefValue.split(",");
		for(String ident : idents) {
			hash.put(ident.trim().toCharArray(), this);
		}
		
		return hash;
	}

}
