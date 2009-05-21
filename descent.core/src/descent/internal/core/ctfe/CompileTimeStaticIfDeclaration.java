package descent.internal.core.ctfe;

import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StaticIfDeclaration;

public class CompileTimeStaticIfDeclaration extends StaticIfDeclaration {

	public CompileTimeStaticIfDeclaration(Condition condition, Dsymbols decl, Dsymbols elsedecl) {
		super(condition, decl, elsedecl);
	}
	
	@Override
	public Dsymbols include(Scope sc, ScopeDsymbol sd, SemanticContext context) {
		if (condition.inc != 0)
			return super.include(sc, sd, context);
		
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, sc);
			
			return super.include(sc, sd, context);
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, sc);
		}
	}

}
