package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.ReturnStatement;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeReturnStatement extends ReturnStatement {

	public CompileTimeReturnStatement(int loc, Expression exp) {
		super(loc, exp);
	}

	public CompileTimeReturnStatement(Loc loc, Expression exp) {
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
