package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TemplateParameters;

public class CompileTimeTemplateDeclaration extends TemplateDeclaration {
	
	private final boolean fIgnoreFirstSemanticAnalysis;
	private boolean fDoneSemanticAnalysis;

	public CompileTimeTemplateDeclaration(char[] filename, int lineNumber, IdentifierExp id, TemplateParameters parameters, Expression constraint, Dsymbols decldefs) {
		this(filename, lineNumber, id, parameters, constraint, decldefs, false);
	}
	
	public CompileTimeTemplateDeclaration(char[] filename, int lineNumber, IdentifierExp id, TemplateParameters parameters, Expression constraint, Dsymbols decldefs, boolean ignoreFirstSemanticAnalysis) {
		super(filename, lineNumber, id, parameters, constraint, decldefs);
		
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
