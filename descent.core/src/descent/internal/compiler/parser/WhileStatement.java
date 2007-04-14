package descent.internal.compiler.parser;


public class WhileStatement extends Statement {
	
	public Expression condition;
	public Statement body;
	
	public WhileStatement(Loc loc, Expression c, Statement b) {
		super(loc);
		this.condition = c;
		this.body = b;
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		condition = condition.semantic(sc, context);
		condition = resolveProperties(sc, condition, context);
		condition = condition.checkToBoolean(context);

		sc.noctor++;

		Scope scd = sc.push();
		scd.sbreak = this;
		scd.scontinue = this;
		body = body.semantic(scd, context);
		scd.pop();

		sc.noctor--;

		return this;
	}
	
	@Override
	public int getNodeType() {
		return WHILE_STATEMENT;
	}

}
