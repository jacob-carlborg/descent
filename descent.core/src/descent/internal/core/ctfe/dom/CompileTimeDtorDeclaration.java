package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.DtorDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeDtorDeclaration extends DtorDeclaration {

	public CompileTimeDtorDeclaration(Loc loc, IdentifierExp id) {
		super(loc, id);
	}

	public CompileTimeDtorDeclaration(Loc loc) {
		super(loc);
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
