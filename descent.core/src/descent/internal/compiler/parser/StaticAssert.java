package descent.internal.compiler.parser;


public class StaticAssert extends Dsymbol {
	
	public Expression exp;
	public Expression msg;

	public StaticAssert(Expression exp, Expression msg) {
		this.exp = exp;
		this.msg = msg;		
	}
	
	@Override
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum, SemanticContext context) {
		return 0; // we didn't add anything
	}
	
	@Override
	public int getNodeType() {
		return STATIC_ASSERT;
	}

}
