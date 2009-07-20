package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.Statements;

public class CompileTimeCompoundStatement extends CompoundStatement {

	public CompileTimeCompoundStatement(char[] filename, int lineNumber, Statement s1, Statement s2) {
		super(filename, lineNumber, s1, s2);
	}

	public CompileTimeCompoundStatement(char[] filename, int lineNumber, Statements statements) {
		super(filename, lineNumber, statements);
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
