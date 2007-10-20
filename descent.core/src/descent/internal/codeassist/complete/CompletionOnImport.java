package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.Loc;

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

	public CompletionOnImport(Loc loc, Identifiers packages, IdentifierExp id, IdentifierExp aliasId, boolean isstatic) {
		super(loc, packages, id, aliasId, isstatic);
	}

}
