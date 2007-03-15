package descent.internal.compiler.parser;

public class StaticAssert extends Dsymbol {
	
	public Expression exp;
	public Expression msg;

	public StaticAssert(Expression exp, Expression msg) {
		this.exp = exp;
		this.msg = msg;		
	}
	
	@Override
	public int kind() {
		return STATIC_ASSERT;
	}

}
