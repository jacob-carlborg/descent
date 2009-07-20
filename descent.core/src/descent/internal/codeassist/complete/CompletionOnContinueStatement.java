package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.ContinueStatement;
import descent.internal.compiler.parser.IdentifierExp;

public class CompletionOnContinueStatement extends ContinueStatement {

	public CompletionOnContinueStatement(char[] filename, int lineNumber, IdentifierExp ident) {
		super(filename, lineNumber, ident);
	}

}
