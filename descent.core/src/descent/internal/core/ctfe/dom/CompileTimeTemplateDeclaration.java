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
	
	private final boolean fIgnoreFirstSemanticAnalysis;
	private boolean fDoneSemanticAnalysis;

	public CompileTimeTemplateDeclaration(Loc loc, IdentifierExp id, TemplateParameters parameters, Expression constraint, Dsymbols decldefs) {
		this(loc, id, parameters, constraint, decldefs, false);
	}
	
	public CompileTimeTemplateDeclaration(Loc loc, IdentifierExp id, TemplateParameters parameters, Expression constraint, Dsymbols decldefs, boolean ignoreFirstSemanticAnalysis) {
		super(loc, id, parameters, constraint, decldefs);
		
		this.fIgnoreFirstSemanticAnalysis = ignoreFirstSemanticAnalysis;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (fIgnoreFirstSemanticAnalysis && !fDoneSemanticAnalysis) {
			fDoneSemanticAnalysis = true;
			super.semantic(sc, context);
			return;
		}
		
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, sc);
			
			super.semantic(sc, context);
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, sc);
		}
	}

}
