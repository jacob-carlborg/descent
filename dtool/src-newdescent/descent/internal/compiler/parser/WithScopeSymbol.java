package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.core.domX.IASTVisitor;

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
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			Assert.failTODO();
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Dsymbol search(Loc loc, String ident, int flags, SemanticContext context) {
		return withstate.exp.type.toDsymbol(null, context).search(loc, ident, 0, context);
	}

}
