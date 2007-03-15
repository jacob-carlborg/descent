package descent.internal.compiler.parser;

import descent.core.IProblemRequestor;

public class SemanticContext {
	
	public IProblemRequestor problemRequestor;
	public StringTable typeStringTable;
	
	public SemanticContext(IProblemRequestor problemRequestor) {
		this.problemRequestor = problemRequestor;
		this.typeStringTable = new StringTable();
	}
	
	

}
