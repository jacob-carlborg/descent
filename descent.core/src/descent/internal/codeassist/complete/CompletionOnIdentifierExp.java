package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
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

	public CompletionOnIdentifierExp(char[] filename, int lineNumber, Token token) {
		super(filename, lineNumber, token);
		this.dot = -1;
	}
	
	public CompletionOnIdentifierExp(char[] filename, int lineNumber, Token token, int dot) {
		super(filename, lineNumber, token);
		this.dot = dot;
	}
	
	public CompletionOnIdentifierExp(char[] filename, int lineNumber, IdentifierExp ident) {
		super(filename, lineNumber, ident);
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression exp = super.semantic(sc, context);
		
		this.scope = ScopeCopy.copy(sc, context);
		
		return exp;
	}

}
