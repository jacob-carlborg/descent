package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeCtorDeclaration extends CtorDeclaration {

	public CompileTimeCtorDeclaration(char[] filename, int lineNumber, Arguments arguments, int varags) {
		super(filename, lineNumber, arguments, varags);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, sc);
			
			super.semantic(sc, context);
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, sc);
		}
	}

}
