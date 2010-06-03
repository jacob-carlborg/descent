package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.ForeachRangeStatement;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.TOK;

public class CompileTimeForeachRangeStatement extends ForeachRangeStatement {

	public CompileTimeForeachRangeStatement(char[] filename, int lineNumber, TOK op, Argument arg, Expression lwr, Expression upr, Statement body) {
		super(filename, lineNumber, op, arg, lwr, upr, body);
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
