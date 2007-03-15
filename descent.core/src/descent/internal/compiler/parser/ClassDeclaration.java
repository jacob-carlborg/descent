package descent.internal.compiler.parser;

import java.util.List;

public class ClassDeclaration extends AggregateDeclaration {

	public List<BaseClass> baseClasses;

	public ClassDeclaration(IdentifierExp id, List<BaseClass> baseClasses) {
		super(id);
		this.baseClasses = baseClasses;
	}
	
	@Override
	public void semantic(Scope scope, SemanticContext context) {
		// TODO stub
		symtab = new DsymbolTable();
		for(Dsymbol s : members) {
    		s.addMember(null, this, 1, context);
    	}
	}
	
	@Override
	public int kind() {
		return CLASS_DECLARATION;
	}

}
