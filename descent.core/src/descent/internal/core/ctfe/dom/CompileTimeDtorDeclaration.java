package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.DtorDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeDtorDeclaration extends DtorDeclaration {

	public CompileTimeDtorDeclaration(char[] filename, int lineNumber, IdentifierExp id) {
		super(filename, lineNumber, id);
	}

	public CompileTimeDtorDeclaration(char[] filename, int lineNumber) {
		super(filename, lineNumber);
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
