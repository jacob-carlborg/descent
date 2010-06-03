package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeCompileDeclaration extends CompileDeclaration {

	public CompileTimeCompileDeclaration(char[] filename, int lineNumber, Expression exp) {
		super(filename, lineNumber, exp);
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
