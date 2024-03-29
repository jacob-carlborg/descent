package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;

public class CompileTimeAliasDeclaration extends AliasDeclaration {

	public CompileTimeAliasDeclaration(char[] filename, int lineNumber, IdentifierExp id, Dsymbol s) {
		super(filename, lineNumber, id, s);
	}

	public CompileTimeAliasDeclaration(char[] filename, int lineNumber, IdentifierExp id, Type type) {
		super(filename, lineNumber, id, type);
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
