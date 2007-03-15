package descent.internal.compiler.parser;

import java.util.List;

public class NewAnonClassExp extends Expression {

	public Expression thisexp;
	public List<Expression> newargs;
	public ClassDeclaration cd;
	public List<Expression> arguments;

	public NewAnonClassExp(Expression thisexp, List<Expression> newargs, ClassDeclaration cd, List<Expression> arguments) {
		super(TOK.TOKnewanonclass);
		this.thisexp = thisexp;
		this.newargs = newargs;
		this.cd = cd;
		this.arguments = arguments;
	}
	
	@Override
	public int kind() {
		return NEW_ANON_CLASS_EXP;
	}

}
