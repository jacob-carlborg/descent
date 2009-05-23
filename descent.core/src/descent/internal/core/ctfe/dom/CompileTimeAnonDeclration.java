package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Loc;

public class CompileTimeAnonDeclration extends AnonDeclaration {

	public CompileTimeAnonDeclration(Loc loc, boolean isunion, Dsymbols decl) {
		super(loc, isunion, decl);
	}

}
