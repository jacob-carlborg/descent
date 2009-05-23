package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.InvariantDeclaration;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeInvariantDeclaration extends InvariantDeclaration {

	public CompileTimeInvariantDeclaration(Loc loc) {
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
