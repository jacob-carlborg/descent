package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeAnonDeclaration extends AnonDeclaration {

	public CompileTimeAnonDeclaration(char[] filename, int lineNumber, boolean isunion, Dsymbols decl) {
		super(filename, lineNumber, isunion, decl);
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
