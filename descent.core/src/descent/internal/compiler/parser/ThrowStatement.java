package descent.internal.compiler.parser;

public class ThrowStatement extends Statement {

	public Expression exp;

	public ThrowStatement(Expression exp) {
		this.exp = exp;		
	}
	
	@Override
	public int getNodeType() {
		return THROW_STATEMENT;
	}

}
