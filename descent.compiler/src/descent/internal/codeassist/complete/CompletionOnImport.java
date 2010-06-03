package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.Import;

/*
 * Completion node build by the parser in any case it was intending to
 * reduce an import containing the cursor location.
 * e.g.
 *
 *  import std.st[cursor];
 *	class X {
 *    void foo() {
 *    }
 *  }
 *
 *	---> <CompleteOnImport:std.st>
 *		 class X {
 *         void foo() {
 *         }
 *       }
 */
public class CompletionOnImport extends Import {
	
	public int completePosition;
	public boolean isSelective;
	public IdentifierExp selectiveName;

	public CompletionOnImport(char[] filename, int lineNumber, Identifiers packages, IdentifierExp id, IdentifierExp aliasId, boolean isstatic, int completePosition) {
		super(filename, lineNumber, packages, id, aliasId, isstatic);
		this.completePosition = completePosition;
	}
	
	public int getFqnStart() {
		return CompletionUtils.getFqnStart(packages, id, completePosition);
	}
	
	public int getFqnEnd() {
		return CompletionUtils.getFqnEnd(packages, id, completePosition);
	}

}
