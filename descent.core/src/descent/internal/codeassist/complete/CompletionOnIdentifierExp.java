package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Token;

public class CompletionOnIdentifierExp extends IdentifierExp {
	
	public Scope scope;

	public CompletionOnIdentifierExp(Loc loc, Token token) {
		super(loc, token);
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression exp = super.semantic(sc, context);
		
		this.scope = ScopeCopy.copy(sc, context);
		
		return exp;
	}

}
