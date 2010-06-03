package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.BreakStatement;
import descent.internal.compiler.parser.IdentifierExp;

public class CompletionOnBreakStatement extends BreakStatement {

	public CompletionOnBreakStatement(char[] filename, int lineNumber, IdentifierExp ident) {
		super(filename, lineNumber, ident);
	}

}
