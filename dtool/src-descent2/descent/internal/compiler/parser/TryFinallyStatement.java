package descent.internal.compiler.parser;


public class TryFinallyStatement extends Statement {
	
	public Statement body;
	public Statement finalbody;
	public boolean isTryCatchFinally;
	
	public TryFinallyStatement(Loc loc, Statement body, Statement finalbody) {
		this(loc, body, finalbody, false);		
	}

	public TryFinallyStatement(Loc loc, Statement body, Statement finalbody, boolean isTryCatchFinally) {
		super(loc);
		this.body = body;
		this.finalbody = finalbody;
		this.isTryCatchFinally = isTryCatchFinally;		
	}

	@Override
	public int getNodeType() {
		return TRY_FINALLY_STATEMENT;
	}

}
