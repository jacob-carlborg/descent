package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;

public class CompletionOnDotIdExp extends DotIdExp {

	public CompletionOnDotIdExp(Loc loc, Expression e, IdentifierExp id) {
		super(loc, e, id);
	}

}
