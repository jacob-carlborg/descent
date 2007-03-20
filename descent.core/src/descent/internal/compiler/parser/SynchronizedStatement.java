package descent.internal.compiler.parser;

public class SynchronizedStatement extends Statement {

	public Expression exp;
	public Statement body;

	public SynchronizedStatement(Expression exp, Statement body) {
		this.exp = exp;
		this.body = body;		
	}
	
	@Override
	public int getNodeType() {
		return SYNCHRONIZED_STATEMENT;
	}

}
