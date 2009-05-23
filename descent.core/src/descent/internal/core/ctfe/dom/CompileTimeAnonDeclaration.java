package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeAnonDeclaration extends AnonDeclaration {

	public CompileTimeAnonDeclaration(Loc loc, boolean isunion, Dsymbols decl) {
		super(loc, isunion, decl);
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
