package descent.internal.compiler.parser;

public class WithScopeSymbol extends ScopeDsymbol {
	
	public WithStatement withstate;

	public WithScopeSymbol(Loc loc, WithStatement withstate) {
		super(loc);
		this.withstate = withstate;
	}
	
	@Override
	public WithScopeSymbol isWithScopeSymbol() {
		return this;
	}
	
	@Override
	public Dsymbol search(Loc loc, String ident, int flags, SemanticContext context) {
		return withstate.exp.type.toDsymbol(null, context).search(loc, ident, 0, context);
	}

}
