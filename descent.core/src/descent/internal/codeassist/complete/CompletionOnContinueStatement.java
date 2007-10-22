package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.ContinueStatement;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;

public class CompletionOnContinueStatement extends ContinueStatement {

	public CompletionOnContinueStatement(Loc loc, IdentifierExp ident) {
		super(loc, ident);
	}

}
