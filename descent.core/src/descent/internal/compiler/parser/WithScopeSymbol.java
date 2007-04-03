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
	
	@Override
	public Dsymbol search(Identifier ident, int flags, SemanticContext context) {
		return withstate.exp.type.toDsymbol(null, context).search(ident, 0, context);
	}

}
