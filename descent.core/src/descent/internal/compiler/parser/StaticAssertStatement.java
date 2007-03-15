package descent.internal.compiler.parser;

public class StaticAssertStatement extends Statement {
	
	public StaticAssert sa;
	
	public StaticAssertStatement(StaticAssert sa) {
		this.sa = sa;
	}
	
	@Override
	public int kind() {
		return STATIC_ASSERT_STATEMENT;
	}

}
