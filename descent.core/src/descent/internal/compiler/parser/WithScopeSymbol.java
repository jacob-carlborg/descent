package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class WithScopeSymbol extends ScopeDsymbol {

	public WithStatement withstate;

	public WithScopeSymbol(Loc loc, WithStatement withstate) {
		super(loc);
		this.withstate = withstate;
	}

	public WithScopeSymbol(WithStatement withstate) {
		this(Loc.ZERO, withstate);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		melnorme.miscutil.Assert.fail("accept0 on a fake Node");
	}

	@Override
	public WithScopeSymbol isWithScopeSymbol() {
		return this;
	}

	@Override
	public Dsymbol search(Loc loc, char[] ident, int flags,
			SemanticContext context) {
		return withstate.exp.type.toDsymbol(null, context).search(loc, ident,
				0, context);
	}

}
