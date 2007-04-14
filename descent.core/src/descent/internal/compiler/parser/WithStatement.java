package descent.internal.compiler.parser;

public class WithStatement extends Statement {

	public Expression exp;
	public Statement body;
	public VarDeclaration wthis;

	public WithStatement(Loc loc, Expression exp, Statement body) {
		super(loc);
		this.exp = exp;
		this.body = body;
	}
	
	@Override
	public int getNodeType() {
		return WITH_STATEMENT;
	}

}
