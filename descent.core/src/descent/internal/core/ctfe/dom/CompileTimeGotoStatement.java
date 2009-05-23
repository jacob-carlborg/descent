package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.GotoStatement;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeGotoStatement extends GotoStatement {

	public CompileTimeGotoStatement(Loc loc, IdentifierExp ident) {
		super(loc, ident);
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
