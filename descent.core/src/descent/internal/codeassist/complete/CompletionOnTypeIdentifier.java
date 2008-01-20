package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeIdentifier;

public class CompletionOnTypeIdentifier extends TypeIdentifier {
	
	public Scope scope;

	public CompletionOnTypeIdentifier(Loc loc, IdentifierExp ident) {
		super(loc, ident);
	}
	
	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		Type type = super.semantic(loc, sc, context);
		
		this.scope = ScopeCopy.copy(sc, context);
		
		return type;
	}

}
