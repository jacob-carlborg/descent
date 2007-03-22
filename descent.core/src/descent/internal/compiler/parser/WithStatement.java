package descent.internal.compiler.parser;

public class WithStatement extends Statement {

	public Expression exp;
	public Statement body;
	public VarDeclaration wthis;

	public WithStatement(Expression exp, Statement body) {
		this.exp = exp;
		this.body = body;
	}
	
	@Override
	public int getNodeType() {
		return WITH_STATEMENT;
	}

}
