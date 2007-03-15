package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import descent.core.IProblemRequestor;
import descent.core.compiler.IProblem;

public class ScopeDsymbol extends Dsymbol {
	
	public List<Dsymbol> members;
	public DsymbolTable symtab;
	
	public ScopeDsymbol() {
	}
	
	public ScopeDsymbol(IdentifierExp id) {
		this.ident = id;
	}
	
	public void addMember(Dsymbol symbol) {
		if (members == null) {
			members = new ArrayList<Dsymbol>();
		}
		members.add(symbol);
	}
	
	@Override
	public int kind() {
		return -1;
	}

	public void multiplyDefined(Dsymbol s1, Dsymbol s2, IProblemRequestor requestor) {
		requestor.acceptProblem(Problem.newSemanticMemberError("Duplicated symbol " + s2.ident.ident.string, IProblem.DuplicatedSymbol, 0, s2.ident.start, s2.ident.length));
		requestor.acceptProblem(Problem.newSemanticMemberError("Duplicated symbol " + s1.ident.ident.string, IProblem.DuplicatedSymbol, 0, s1.ident.start, s1.ident.length));		
	}

}
