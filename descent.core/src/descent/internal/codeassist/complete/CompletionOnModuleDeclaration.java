package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.ModuleDeclaration;

/*
 * Completion node build by the parser in any case it was intending to
 * reduce a module containing the cursor location.
 * e.g.
 *
 *  module std.st[cursor];
 *	class X {
 *    void foo() {
 *    }
 *  }
 *
 *	---> <CompleteOnModule:std.st>
 *		 class X {
 *         void foo() {
 *         }
 *       }
 */
public class CompletionOnModuleDeclaration extends ModuleDeclaration {
	
	public int completePosition;

	public CompletionOnModuleDeclaration(Identifiers packages, IdentifierExp id, int completePosition) {
		super(packages, id);
		this.completePosition = completePosition;
	}
	
	public int getFqnStart() {
		return CompletionUtils.getFqnStart(packages, id, completePosition);
	}
	
	public int getFqnEnd() {
		return CompletionUtils.getFqnEnd(packages, id, completePosition);
	}

}
