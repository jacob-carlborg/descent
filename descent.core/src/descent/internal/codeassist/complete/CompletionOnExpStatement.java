package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;

public class CompletionOnExpStatement extends ExpStatement {
	
	public Scope scope;

	public CompletionOnExpStatement(char[] filename, int lineNumber, Expression exp) {
		super(filename, lineNumber, exp);
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		Statement statement =  super.semantic(sc, context);
		
		this.scope = ScopeCopy.copy(sc, context);
		
		return statement;
	}

}
