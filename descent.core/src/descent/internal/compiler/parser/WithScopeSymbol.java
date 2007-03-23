package descent.internal.compiler.parser;

public class WithScopeSymbol extends ScopeDsymbol {
	
	public WithStatement withstate;

	public WithScopeSymbol(WithStatement withstate) {
		this.withstate = withstate;
	}
	
	@Override
	public WithScopeSymbol isWithScopeSymbol() {
		return this;
	}

}
