package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.TryFinallyStatement;

public class CompileTimeTryFinallyStatement extends TryFinallyStatement {

	public CompileTimeTryFinallyStatement(char[] filename, int lineNumber, Statement body, Statement finalbody, boolean isTryCatchFinally) {
		super(filename, lineNumber, body, finalbody, isTryCatchFinally);
	}

	public CompileTimeTryFinallyStatement(char[] filename, int lineNumber, Statement body, Statement finalbody) {
		super(filename, lineNumber, body, finalbody);
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
