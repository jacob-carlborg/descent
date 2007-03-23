package descent.internal.compiler.parser;

public class ExpStatement extends Statement {
	
	public Expression exp;

	public ExpStatement(Expression exp) {
		this.exp = exp;		
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		if (exp != null) {
			exp = exp.semantic(sc, context);
			exp = Expression.resolveProperties(sc, exp, context);
			exp.checkSideEffect(0);
			exp = exp.optimize(0);
		}
		return this;
	}
	
	@Override
	public int getNodeType() {
		return EXP_STATEMENT;
	}

}
