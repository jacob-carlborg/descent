package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;

public class CompletionOnDotIdExp extends DotIdExp {

	public CompletionOnDotIdExp(char[] filename, int lineNumber, Expression e, IdentifierExp id) {
		super(filename, lineNumber, e, id);
	}

}
