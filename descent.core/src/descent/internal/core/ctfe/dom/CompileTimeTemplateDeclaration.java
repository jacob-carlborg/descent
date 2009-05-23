package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TemplateParameters;

public class CompileTimeTemplateDeclaration extends TemplateDeclaration {

	public CompileTimeTemplateDeclaration(Loc loc, IdentifierExp id, TemplateParameters parameters, Expression constraint, Dsymbols decldefs) {
		super(loc, id, parameters, constraint, decldefs);
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
