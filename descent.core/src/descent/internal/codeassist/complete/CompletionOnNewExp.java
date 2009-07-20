package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.NewExp;
import descent.internal.compiler.parser.Type;

public class CompletionOnNewExp extends NewExp {

	public CompletionOnNewExp(char[] filename, int lineNumber, Expression thisexp, Expressions newargs, Type newtype, Expressions arguments) {
		super(filename, lineNumber, thisexp, newargs, newtype, arguments);
	}

}
