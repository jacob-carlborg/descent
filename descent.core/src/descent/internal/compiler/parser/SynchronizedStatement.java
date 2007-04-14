package descent.internal.compiler.parser;

public class SynchronizedStatement extends Statement {

	public Expression exp;
	public Statement body;

	public SynchronizedStatement(Loc loc, Expression exp, Statement body) {
		super(loc);
		this.exp = exp;
		this.body = body;		
	}
	
	@Override
	public int getNodeType() {
		return SYNCHRONIZED_STATEMENT;
	}

}
