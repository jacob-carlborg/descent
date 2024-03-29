package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeInterfaceDeclaration extends InterfaceDeclaration {

	public CompileTimeInterfaceDeclaration(char[] filename, int lineNumber, IdentifierExp id, BaseClasses baseclasses) {
		super(filename, lineNumber, id, baseclasses);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (sc.parent instanceof FuncDeclaration) {
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
