package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.Module;

public class CompletionOnDebugCondition extends DebugCondition {

	public CompletionOnDebugCondition(Module mod, char[] filename, int lineNumber, long level, char[] id) {
		super(mod, filename, lineNumber, level, id);
	}

}
