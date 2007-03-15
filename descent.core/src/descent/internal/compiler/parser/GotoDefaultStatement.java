package descent.internal.compiler.parser;

public class GotoDefaultStatement extends Statement {
	
	@Override
	public int kind() {
		return GOTO_DEFAULT_STATEMENT;
	}

}
