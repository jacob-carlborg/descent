package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.GotoDefaultStatement;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimeGotoDefaultStatement extends GotoDefaultStatement {
	
	public CompileTimeGotoDefaultStatement(char[] filename, int lineNumber) {
		super(filename, lineNumber);
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
