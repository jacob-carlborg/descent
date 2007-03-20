package descent.internal.compiler.parser;

import java.util.List;

public class ArrayExp extends UnaExp {

	public List<Expression> arguments;

	public ArrayExp(Expression e, List<Expression> arguments) {
		super(TOK.TOKarray, e);
		this.arguments = arguments;
	}
	
	@Override
	public int getNodeType() {
		return ARRAY_EXP;
	}

}
