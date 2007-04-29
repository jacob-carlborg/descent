package descent.internal.compiler.parser;


public class DoStatement extends Statement {
	
	public Expression condition;
	public Statement body;
	
	public DoStatement(Loc loc, Statement b, Expression c) {
		super(loc);
		this.condition = c;
		this.body = b;
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		sc.noctor++;
	    body = body.semanticScope(sc, this, this, context);
	    sc.noctor--;
	    condition = condition.semantic(sc, context);
	    condition = resolveProperties(sc, condition, context);

	    condition = condition.checkToBoolean(context);

	    return this;
	}
	
	@Override
	public int getNodeType() {
		return DO_STATEMENT;
	}

}
