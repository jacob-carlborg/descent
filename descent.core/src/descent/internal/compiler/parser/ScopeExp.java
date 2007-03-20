package descent.internal.compiler.parser;

public class ScopeExp extends Expression {
	
	public ScopeDsymbol sds;

	public ScopeExp(ScopeDsymbol sds) {
		super(TOK.TOKimport);
		this.sds = sds;		
	}
	
	@Override
	public int getNodeType() {
		return SCOPE_EXP;
	}

}
