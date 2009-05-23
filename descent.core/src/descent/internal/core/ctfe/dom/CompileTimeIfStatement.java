package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IfStatement;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;

public class CompileTimeIfStatement extends IfStatement {

	public CompileTimeIfStatement(Loc loc, Argument arg, Expression condition,
			Statement ifbody, Statement elsebody) {
		super(loc, arg, condition, ifbody, elsebody);
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
