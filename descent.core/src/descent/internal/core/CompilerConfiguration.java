package descent.internal.core;

import org.eclipse.core.resources.ResourcesPlugin;

import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.core.util.Util;

public class CompilerConfiguration {
	
	private IJavaProject javaProject;
	
	// 0: no, 1: some, 2: full
	public int semanticAnalysisLevel;
	public long versionLevel;
	public HashtableOfCharArrayAndObject versionIdentifiers;
	public long debugLevel;
	public HashtableOfCharArrayAndObject debugIdentifiers;
	public boolean useDeprecated;
	public boolean warnings;
	
	public CompilerConfiguration() {
		this(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getActiveProject());
	}
	
	public CompilerConfiguration(IJavaProject javaProject) {
		this.javaProject = javaProject;
		
		semanticAnalysisLevel = (int) getLevel(JavaCore.COMPILER_SHOW_SEMANTIC_ERRORS);
//		semanticAnalysisLevel = 0;
		
		debugLevel = getLevel(JavaCore.COMPILER_DEBUG_LEVEL);
		debugIdentifiers = getIdentifiers(JavaCore.COMPILER_DEBUG_IDENTIFIERS);
		
		versionLevel = getLevel(JavaCore.COMPILER_DEBUG_LEVEL);
		versionIdentifiers = getIdentifiers(JavaCore.COMPILER_VERSION_IDENTIFIERS);
		
		useDeprecated = getOption(JavaCore.COMPILER_ALLOW_DEPRECATED).equals(JavaCore.ENABLED);
		warnings = getOption(JavaCore.COMPILER_ENABLE_WARNINGS).equals(JavaCore.ENABLED);
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
	
	private long getLevel(String prefKey) {
		String level = getOption(prefKey);
		try {
			return Long.parseLong(level);
		} catch (NumberFormatException e) {
			Util.log(e);
			return 0;
		}
	}
	
	private HashtableOfCharArrayAndObject getIdentifiers(String pref) {
		HashtableOfCharArrayAndObject hash = new HashtableOfCharArrayAndObject();
		
		String prefValue = getOption(pref);		
		String[] idents = prefValue.split(",");
		for(String ident : idents) {
			hash.put(ident.trim().toCharArray(), this);
		}
		
		return hash;
	}
	
	private String getOption(String pref) {
		if (javaProject == null) {
			return JavaCore.getOption(pref);
		} else {
			return javaProject.getOption(pref, true);
		}
	}

}
