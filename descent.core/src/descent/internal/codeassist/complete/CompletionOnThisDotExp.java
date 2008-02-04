package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.ThisExp;

public class CompletionOnThisDotExp extends ThisExp {

	public CompletionOnThisDotExp(Loc loc) {
		super(loc);
	}

}
