package descent.internal.compiler.parser;

import java.util.List;

public class ArrayExp extends UnaExp {

	public List<Expression> arguments;

	public ArrayExp(Loc loc, Expression e, List<Expression> arguments) {
		super(loc, TOK.TOKarray, e);
		this.arguments = arguments;
	}
	
	@Override
	public int getNodeType() {
		return ARRAY_EXP;
	}

}
