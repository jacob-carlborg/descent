package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.UnitTestDeclaration;

public class CompileTimeUnitTestDeclaration extends UnitTestDeclaration {

	public CompileTimeUnitTestDeclaration(char[] filename, int lineNumber) {
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
