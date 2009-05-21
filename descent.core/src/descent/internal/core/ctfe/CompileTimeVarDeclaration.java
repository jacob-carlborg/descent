package descent.internal.core.ctfe;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.VarDeclaration;

public class CompileTimeVarDeclaration extends VarDeclaration {

	public CompileTimeVarDeclaration(Loc loc, Type type, char[] ident, Initializer init) {
		super(loc, type, ident, init);
	}

	public CompileTimeVarDeclaration(Loc loc, Type type, IdentifierExp id, Initializer init) {
		super(loc, type, id, init);
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
