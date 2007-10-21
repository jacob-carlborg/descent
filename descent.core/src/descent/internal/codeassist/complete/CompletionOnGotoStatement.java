package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.GotoStatement;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;

public class CompletionOnGotoStatement extends GotoStatement {

	public CompletionOnGotoStatement(Loc loc, IdentifierExp ident) {
		super(loc, ident);
	}

}
