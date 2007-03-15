package descent.internal.compiler.parser;

import java.util.List;

import descent.core.IProblemRequestor;

public class ClassDeclaration extends AggregateDeclaration {

	public List<BaseClass> baseClasses;

	public ClassDeclaration(IdentifierExp id, List<BaseClass> baseClasses) {
		super(id);
		this.baseClasses = baseClasses;
	}
	
	@Override
	public void semantic(Scope scope, IProblemRequestor problemRequestor) {
		// TODO stub
		symtab = new DsymbolTable();
		for(Dsymbol s : members) {
    		s.addMember(null, this, 1, problemRequestor);
    	}
	}
	
	@Override
	public int kind() {
		return CLASS_DECLARATION;
	}

}
