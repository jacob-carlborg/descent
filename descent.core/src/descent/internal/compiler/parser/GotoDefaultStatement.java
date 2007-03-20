package descent.internal.compiler.parser;

public class GotoDefaultStatement extends Statement {
	
	@Override
	public int getNodeType() {
		return GOTO_DEFAULT_STATEMENT;
	}

}
