package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.AlignDeclaration;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeAlignDeclaration extends AlignDeclaration {

	public CompileTimeAlignDeclaration(int sa, Dsymbols decl) {
		super(sa, decl);
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
