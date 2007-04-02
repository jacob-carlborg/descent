package descent.internal.compiler.parser;

public class Tuple extends ASTNode {
	
	@Override
	public DYNCAST dyncast() {
		return DYNCAST.DYNCAST_TUPLE;
	}

	@Override
	public int getNodeType() {
		return 0;
	}

}
