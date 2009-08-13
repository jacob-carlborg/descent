package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.CaseRangeStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;

public class CompileTimeCaseRangeStatement extends CaseRangeStatement {

	public CompileTimeCaseRangeStatement(char[] filename, int lineNumber,
			Expression first, Expression last, Statement statement) {
		super(filename, lineNumber, first, last, statement);
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
