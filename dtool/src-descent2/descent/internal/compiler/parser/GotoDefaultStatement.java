package descent.internal.compiler.parser;

public class GotoDefaultStatement extends Statement {
	
	public GotoDefaultStatement(Loc loc) {
		super(loc);
	}

	@Override
	public int getNodeType() {
		return GOTO_DEFAULT_STATEMENT;
	}

}
