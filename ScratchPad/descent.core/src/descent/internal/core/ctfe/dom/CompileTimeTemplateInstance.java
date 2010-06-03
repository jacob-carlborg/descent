package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TemplateInstance;

public class CompileTimeTemplateInstance extends TemplateInstance {
	
	private boolean fSemanticRun;

	public CompileTimeTemplateInstance(char[] filename, int lineNumber, IdentifierExp id,
			ASTNodeEncoder encoder) {
		super(filename, lineNumber, id, encoder);
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
