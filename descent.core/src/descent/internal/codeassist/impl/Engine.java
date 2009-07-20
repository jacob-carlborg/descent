package descent.internal.codeassist.impl;

import java.util.Map;

import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.core.SearchableEnvironment;

public abstract class Engine {
	
	public AssistOptions options;
	public CompilerOptions compilerOptions; 
	
	protected SearchableEnvironment nameEnvironment;
	
	public Engine(Map settings){
		this.options = new AssistOptions(settings);
		this.compilerOptions = new CompilerOptions(settings);
	}

}
