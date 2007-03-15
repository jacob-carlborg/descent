package descent.internal.compiler.parser;


public abstract class UnaExp extends Expression {
	
	public Expression e1;

	public UnaExp(TOK op, Expression e1) {
		super(op);
		this.e1 = e1;		
	}

}
