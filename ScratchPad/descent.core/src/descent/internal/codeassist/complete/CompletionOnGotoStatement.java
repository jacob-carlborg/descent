package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.GotoStatement;
import descent.internal.compiler.parser.IdentifierExp;

public class CompletionOnGotoStatement extends GotoStatement {

	public CompletionOnGotoStatement(char[] filename, int lineNumber, IdentifierExp ident) {
		super(filename, lineNumber, ident);
	}

}
