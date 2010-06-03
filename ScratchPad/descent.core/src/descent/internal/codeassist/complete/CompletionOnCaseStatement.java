package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Statement;

public class CompletionOnCaseStatement extends CaseStatement {
	
	public Expression originalExpression;

	public CompletionOnCaseStatement(char[] filename, int lineNumber, Expression exp, Statement s) {
		super(filename, lineNumber, exp, s);
		this.originalExpression = exp;
	}

}
