package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;

public class CompileTimeCaseStatement extends CaseStatement {

	public CompileTimeCaseStatement(char[] filename, int lineNumber, Expression exp, Statement s) {
		super(filename, lineNumber, exp, s);
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
