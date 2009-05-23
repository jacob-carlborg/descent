package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Objects;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TemplateInstance;

public class CompileTimeTemplateInstance extends TemplateInstance {

	public CompileTimeTemplateInstance(Loc loc, IdentifierExp id,
			ASTNodeEncoder encoder) {
		super(loc, id, encoder);
	}

	public CompileTimeTemplateInstance(Loc loc, TemplateDeclaration td,
			Objects tiargs, ASTNodeEncoder encoder) {
		super(loc, td, tiargs, encoder);
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
