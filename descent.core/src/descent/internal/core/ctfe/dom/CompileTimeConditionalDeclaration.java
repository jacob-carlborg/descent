package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeConditionalDeclaration extends ConditionalDeclaration {

	public CompileTimeConditionalDeclaration(Condition condition, Dsymbols decl, Dsymbols elsedecl) {
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
