package descent.internal.compiler.parser;

import java.util.List;

public class CallExp extends UnaExp {

	public List<Expression> arguments;

	public CallExp(Expression e, List<Expression> exps) {
		super(TOK.TOKcall, e);
		this.arguments = exps;		
	}
	
	@Override
	public int kind() {
		return CALL_EXP;
	}

}
