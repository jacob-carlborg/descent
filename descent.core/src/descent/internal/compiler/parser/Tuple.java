package descent.internal.compiler.parser;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class Tuple extends ASTDmdNode {

	public Objects objects;

	@Override
	public void accept0(IASTVisitor visitor) {
		Assert.fail("Fake Node accept0");
	}

	@Override
	public DYNCAST dyncast() {
		return DYNCAST.DYNCAST_TUPLE;
	}

	@Override
	public int getNodeType() {
		return 0;
	}

}
