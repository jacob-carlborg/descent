package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompletionOnInterfaceDeclaration extends InterfaceDeclaration {
	
	public Scope theScope;
	public int baseClassIndex = -1;

	public CompletionOnInterfaceDeclaration(Loc loc, IdentifierExp id,
			BaseClasses baseclasses) {
		super(loc, id, baseclasses);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
		
		this.theScope = ScopeCopy.copy(sc, context);
	}

}
