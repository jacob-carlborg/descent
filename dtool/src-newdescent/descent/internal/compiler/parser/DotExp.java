package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.core.domX.IASTVisitor;

public class DotExp extends BinExp {

	public DotExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKdot, e1, e2);
	}

	@Override
	public int getNodeType() {
		return 0;
	}
	
	public void accept0(IASTVisitor visitor) {
		Assert.fail("accept0 on fake node");
	}

}
