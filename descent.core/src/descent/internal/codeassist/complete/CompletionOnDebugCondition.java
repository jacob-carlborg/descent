package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.VersionCondition;

public class CompletionOnDebugCondition extends DebugCondition {

	public CompletionOnDebugCondition(Module mod, Loc loc, long level, char[] id) {
		super(mod, loc, level, id);
	}

}
