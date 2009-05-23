package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;

public class CompileTimeEnumDeclaration extends EnumDeclaration {

	public CompileTimeEnumDeclaration(Loc loc, IdentifierExp id, Type memtype) {
		super(loc, id, memtype);
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
