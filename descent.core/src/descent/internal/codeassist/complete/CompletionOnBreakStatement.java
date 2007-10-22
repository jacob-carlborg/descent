package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.BreakStatement;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;

public class CompletionOnBreakStatement extends BreakStatement {

	public CompletionOnBreakStatement(Loc loc, IdentifierExp ident) {
		super(loc, ident);
	}

}
