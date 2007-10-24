package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeDotIdExp;

public class CompletionOnTypeDotIdExp extends TypeDotIdExp {

	public CompletionOnTypeDotIdExp(Loc loc, Type type, IdentifierExp ident) {
		super(loc, type, ident);
	}

}
