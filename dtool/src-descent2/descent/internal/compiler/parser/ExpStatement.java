package descent.internal.compiler.parser;

public class ExpStatement extends Statement {
	
	public Expression exp;

	public ExpStatement(Loc loc, Expression exp) {
		super(loc);
		this.exp = exp;		
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		if (exp != null) {
			exp = exp.semantic(sc, context);
			exp = Expression.resolveProperties(sc, exp, context);
			exp.checkSideEffect(0, context);
			exp = exp.optimize(0);
		}
		return this;
	}
	
	@Override
	public int getNodeType() {
		return EXP_STATEMENT;
	}

}