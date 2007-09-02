package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class ArrayLengthExp extends UnaExp {

	public ArrayLengthExp(Loc loc, Expression e1) {
		super(loc, TOK.TOKarraylength, e1);
	}

	@Override
	public int getNodeType() {
		return 0;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
	}

}
