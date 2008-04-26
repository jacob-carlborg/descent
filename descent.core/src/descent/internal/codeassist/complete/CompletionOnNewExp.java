package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.NewExp;
import descent.internal.compiler.parser.Type;

public class CompletionOnNewExp extends NewExp {

	public CompletionOnNewExp(Loc loc, Expression thisexp, Expressions newargs, Type newtype, Expressions arguments) {
		super(loc, thisexp, newargs, newtype, arguments);
	}

}
