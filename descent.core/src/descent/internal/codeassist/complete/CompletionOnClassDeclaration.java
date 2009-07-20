package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompletionOnClassDeclaration extends ClassDeclaration {
	
	public Scope theScope;
	public int baseClassIndex = -1;
	public boolean isCompletingScope = false;

	public CompletionOnClassDeclaration(char[] filename, int lineNumber, IdentifierExp id,
			BaseClasses baseclasses) {
		super(filename, lineNumber, id, baseclasses);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
		
		this.theScope = ScopeCopy.copy(sc, context);
	}

}
