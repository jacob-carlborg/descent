package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.ReturnStatement;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;

public class CompletionOnReturnStatement extends ReturnStatement {
	
	public Scope scope;

	public CompletionOnReturnStatement(char[] filename, int lineNumber, Expression exp) {
		super(filename, lineNumber, exp);
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		this.scope = Scope.copy(sc);
		return super.semantic(sc, context);
	}

}
