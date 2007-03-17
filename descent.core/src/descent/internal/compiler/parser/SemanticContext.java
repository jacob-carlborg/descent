package descent.internal.compiler.parser;

import descent.core.IProblemRequestor;
import descent.core.compiler.IProblem;

public class SemanticContext {
	
	private IProblemRequestor problemRequestor;
	public StringTable typeStringTable;
	
	public SemanticContext(IProblemRequestor problemRequestor) {
		this.problemRequestor = problemRequestor;
		this.typeStringTable = new StringTable();
	}
	
	public void acceptProblem(IProblem problem) {
		problemRequestor.acceptProblem(problem);
	}
	
	public void multiplyDefined(Dsymbol s1, Dsymbol s2) {
		acceptProblem(Problem.newSemanticMemberError("Duplicated symbol " + s2.ident.ident.string, IProblem.DuplicatedSymbol, 0, s2.ident.start, s2.ident.length));
		acceptProblem(Problem.newSemanticMemberError("Duplicated symbol " + s1.ident.ident.string, IProblem.DuplicatedSymbol, 0, s1.ident.start, s1.ident.length));		
	}

}
