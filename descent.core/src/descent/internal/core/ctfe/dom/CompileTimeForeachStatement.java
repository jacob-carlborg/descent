package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.ForeachStatement;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.TOK;

public class CompileTimeForeachStatement extends ForeachStatement {

	public CompileTimeForeachStatement(Loc loc, TOK op, Arguments arguments, Expression aggr, Statement body) {
		super(loc, op, arguments, aggr, body);
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
