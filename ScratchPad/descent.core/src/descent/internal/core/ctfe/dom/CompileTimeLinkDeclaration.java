package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.LinkDeclaration;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeLinkDeclaration extends LinkDeclaration {

	public CompileTimeLinkDeclaration(LINK linkage, Dsymbols decl) {
		super(linkage, decl);
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
