package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.Loc;

public class CompletionOnCallExp extends CallExp {

	public CompletionOnCallExp(Loc loc, Expression e, Expressions exps) {
		super(loc, e, exps);
	}
	
	

}
