package descent.internal.compiler.parser;

public class CompileStatement extends Statement {
	
	public Expression exp;

	public CompileStatement(Expression exp) {
		this.exp = exp;	}

	@Override
	public int getNodeType() {
		return COMPILE_STATEMENT;
	}

}
