package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Type;

/*
 * Completion node build by the parser in any case it was intending to
 * reduce an argument name containing the cursor location.
 * e.g.
 *
 *  void foo(SomeType s[cursor]) {
 *  }
 *  
 *	---> void foo(SomeType <CompletionOnArgumentName:s[cursor]>) {
 *       }
 */
public class CompletionOnArgumentName extends Argument {

	public CompletionOnArgumentName(int storageClass, Type type, IdentifierExp ident, Expression defaultArg) {
		super(storageClass, type, ident, defaultArg);
	}

}
