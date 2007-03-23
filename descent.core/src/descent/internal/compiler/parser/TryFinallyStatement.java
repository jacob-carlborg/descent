package descent.internal.compiler.parser;


public class TryFinallyStatement extends Statement {
	
	public Statement body;
	public Statement finalbody;
	public boolean isTryCatchFinally;
	
	public TryFinallyStatement(Statement body, Statement finalbody) {
		this(body, finalbody, false);		
	}

	public TryFinallyStatement(Statement body, Statement finalbody, boolean isTryCatchFinally) {
		this.body = body;
		this.finalbody = finalbody;
		this.isTryCatchFinally = isTryCatchFinally;		
	}

	@Override
	public int getNodeType() {
		return TRY_FINALLY_STATEMENT;
	}

}
