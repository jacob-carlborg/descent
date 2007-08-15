package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.core.domX.IASTVisitor;

public class Tuple extends ASTDmdNode {
	
	@Override
	public DYNCAST dyncast() {
		return DYNCAST.DYNCAST_TUPLE;
	}

	@Override
	public int getNodeType() {
		return 0;
	}
	
	public void accept0(IASTVisitor visitor) {
		Assert.fail("Fake Node accept0");
	}

}
