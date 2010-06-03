package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.DoStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;

public class CompileTimeDoStatement extends DoStatement {

	public CompileTimeDoStatement(char[] filename, int lineNumber, Statement b, Expression c) {
		super(filename, lineNumber, b, c);
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
