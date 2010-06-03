package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.ThrowStatement;

public class CompileTimeThrowStatement extends ThrowStatement {

	public CompileTimeThrowStatement(char[] filename, int lineNumber, Expression exp) {
		super(filename, lineNumber, exp);
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
