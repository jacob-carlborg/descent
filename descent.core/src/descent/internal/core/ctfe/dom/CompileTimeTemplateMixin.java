package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Objects;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.Type;

public class CompileTimeTemplateMixin extends TemplateMixin {
	
	private boolean fSemanticRun;

	public CompileTimeTemplateMixin(Loc loc, IdentifierExp ident, Type tqual, Identifiers idents, Objects tiargs, ASTNodeEncoder encoder) {
		super(loc, ident, tqual, idents, tiargs, encoder);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (fSemanticRun) {
			super.semantic(sc, context);
			return;
		}
		
		fSemanticRun = true;
		
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, sc);
			
			super.semantic(sc, context);
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, sc);
		}
	}

}
