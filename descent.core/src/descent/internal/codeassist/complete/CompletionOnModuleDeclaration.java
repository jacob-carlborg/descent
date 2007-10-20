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
	
	static int getModuleNameStart(Identifiers packages, IdentifierExp id, int completePosition) {
		if (packages != null) {
			return packages.get(0).start;
		} else if (id != null) {
			return id.start;
		} else {
			return completePosition;
		}
	}
	
	static int getModuleNameEnd(Identifiers packages, IdentifierExp id, int completePosition) {
		if (id != null) {
			return id.start + id.length;
		} else if (packages != null) {
			IdentifierExp last = packages.get(packages.size() - 1);
			int ret = last.start + last.length;
			if (completePosition == ret + 1) {
				return completePosition;
			} else {
				return ret;
			}
		} else {
			return completePosition;
		}
	}
	
	public int getModuleNameStart() {
		return getModuleNameStart(packages, id, completePosition);
	}
	
	public int getModuleNameEnd() {
		return getModuleNameEnd(packages, id, completePosition);
	}

}
