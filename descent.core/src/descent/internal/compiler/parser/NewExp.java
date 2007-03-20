package descent.internal.compiler.parser;

import java.util.List;

public class NewExp extends Expression {

	public Expression thisexp;
	public List<Expression> newargs;
	public Type newtype;
	public List<Expression> arguments;
	public boolean onstack;		// allocate on stack

	public NewExp(Expression thisexp, List<Expression> newargs, Type newtype, List<Expression> arguments) {
		super(TOK.TOKnew);
		this.thisexp = thisexp;
		this.newargs = newargs;
		this.newtype = newtype;
		this.arguments = arguments;		
	}
	
	@Override
	public int getNodeType() {
		return NEW_EXP;
	}

}
