package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;

public class CompletionOnCallExp extends CallExp {

	public CompletionOnCallExp(char[] filename, int lineNumber, Expression e, Expressions exps) {
		super(filename, lineNumber, e, exps);
	}
	
	

}
