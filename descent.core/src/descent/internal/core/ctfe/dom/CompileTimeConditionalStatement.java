package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;

public class CompileTimeConditionalStatement extends ConditionalStatement {

	public CompileTimeConditionalStatement(Loc loc, Condition condition, Statement ifbody, Statement elsebody) {
		super(loc, condition, ifbody, elsebody);
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
