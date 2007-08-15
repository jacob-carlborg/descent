package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.core.domX.IASTVisitor;

public class HaltExp extends Expression {

	public HaltExp(Loc loc) {
		super(loc, TOK.TOKhalt);
	}
	
	public void accept0(IASTVisitor visitor) {
		Assert.fail("Accept0 fake node");
	}
	
	@Override
	public int checkSideEffect(int flag, SemanticContext context) {
		return 1;
	}
	
	@Override
	public int getNodeType() {
		return HALT_EXP;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		type = Type.tvoid;
	    return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("halt");
	}

}
