package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.SwitchStatement;

public class CompileTimeSwitchStatement extends SwitchStatement {

	public CompileTimeSwitchStatement(char[] filename, int lineNumber, Expression c, Statement b, boolean isfinal) {
		super(filename, lineNumber, c, b, isfinal);
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
