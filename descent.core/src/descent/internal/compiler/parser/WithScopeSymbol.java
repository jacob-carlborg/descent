package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class WithScopeSymbol extends ScopeDsymbol {

	public WithStatement withstate;

	public WithScopeSymbol(WithStatement withstate) {
		this.withstate = withstate;
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
	public IDsymbol search(Loc loc, char[] ident, int flags,
			SemanticContext context) {
		return withstate.exp.type.toDsymbol(null, context).search(loc, ident,
				0, context);
	}

}
