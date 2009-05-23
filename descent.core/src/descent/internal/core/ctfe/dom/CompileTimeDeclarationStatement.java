package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.DeclarationStatement;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeDeclarationStatement extends DeclarationStatement {

	public CompileTimeDeclarationStatement(Loc loc, Dsymbol s) {
		super(loc, s);
	}

	public CompileTimeDeclarationStatement(Loc loc, Expression exp) {
		super(loc, exp);
	}
	
	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, istate);
			
			return super.interpret(istate, context);
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, istate);
		}
	}

}
