package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.SliceExp;

public class CompletionOnSliceExp extends SliceExp {
	
	public Scope scope;

	public CompletionOnSliceExp(char[] filename, int lineNumber, Expression e1, Expression lwr, Expression upr) {
		super(filename, lineNumber, e1, lwr, upr);
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression exp = super.semantic(sc, context);
		
		this.scope = ScopeCopy.copy(sc, context);
		
		return exp;
	}

}
