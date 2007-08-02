package descent.internal.compiler.parser;

public class ForeachRangeStatement extends Statement {

	public TOK op;
	public Argument arg;
	public Expression lwr;
	public Expression upr;
	public Statement body;

	public ForeachRangeStatement(Loc loc, TOK op, Argument arg, Expression lwr, Expression upr, Statement body) {
		super(loc);
		
		this.op = op;
		this.arg = arg;
		this.lwr = lwr;
		this.upr = upr;
		this.body = body;
	}

	@Override
	public int getNodeType() {
		return FOREACH_RANGE_STATEMENT;
	}

}
