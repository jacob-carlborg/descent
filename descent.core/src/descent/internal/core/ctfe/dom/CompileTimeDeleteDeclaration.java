package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.DeleteDeclaration;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeDeleteDeclaration extends DeleteDeclaration {

	public CompileTimeDeleteDeclaration(Loc loc, Arguments arguments) {
		super(loc, arguments);
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