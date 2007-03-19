package descent.internal.compiler.parser;

import descent.core.IProblemRequestor;
import descent.core.compiler.IProblem;

public class SemanticContext {
	
	private IProblemRequestor problemRequestor;
	public StringTable typeStringTable;
	public Global global = new Global();
	public ClassDeclaration object = new ClassDeclaration(null, null); // ClassDeclaration::object
	
	public SemanticContext(IProblemRequestor problemRequestor) {
		this.problemRequestor = problemRequestor;
		this.typeStringTable = new StringTable();
	}
	
	public void acceptProblem(IProblem problem) {
		problemRequestor.acceptProblem(problem);
	}
	
	public void multiplyDefined(Dsymbol s1, Dsymbol s2) {
		acceptProblem(Problem.newSemanticMemberError("Duplicated symbol " + s2.ident, IProblem.DuplicatedSymbol, 0, s2.ident.start, s2.ident.length));
		acceptProblem(Problem.newSemanticMemberError("Duplicated symbol " + s1.ident, IProblem.DuplicatedSymbol, 0, s1.ident.start, s1.ident.length));		
	}
	
	private int generatedIds;
	public IdentifierExp generateId(String prefix) {
		String name = prefix + ++generatedIds;
		Identifier id = new Identifier(name, TOK.TOKidentifier);
		return new IdentifierExp(id);
	}

}
