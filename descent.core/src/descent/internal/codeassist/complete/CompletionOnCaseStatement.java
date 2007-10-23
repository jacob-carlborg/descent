package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Statement;

public class CompletionOnCaseStatement extends CaseStatement {
	
	public Expression originalExpression;

	public CompletionOnCaseStatement(Loc loc, Expression exp, Statement s) {
		super(loc, exp, s);
		this.originalExpression = exp;
	}

}
