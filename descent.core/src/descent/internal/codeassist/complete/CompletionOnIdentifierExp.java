package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Token;

public class CompletionOnIdentifierExp extends IdentifierExp {
	
	public Scope scope;
	
	/*
	 * If it's type.|
	 * then dot is the cursor position. Else, it's -1.
	 */
	public int dot;

	public CompletionOnIdentifierExp(Loc loc, Token token) {
		super(loc, token);
		this.dot = -1;
	}
	
	public CompletionOnIdentifierExp(Loc loc, Token token, int dot) {
		super(loc, token);
		this.dot = dot;
	}
	
	public CompletionOnIdentifierExp(Loc loc, IdentifierExp ident) {
		super(loc, ident);
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression exp = super.semantic(sc, context);
		
		this.scope = ScopeCopy.copy(sc, context);
		
		return exp;
	}

}
