package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.VersionCondition;

public class CompletionOnVersionCondition extends VersionCondition {

	public CompletionOnVersionCondition(Module mod, char[] filename, int lineNumber, long level, char[] id) {
		super(mod, filename, lineNumber, level, id);
	}

}
