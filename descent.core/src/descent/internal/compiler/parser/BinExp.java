package descent.internal.compiler.parser;

public abstract class BinExp extends Expression {
	
	public Expression e1;
	public Expression e2;
	
	public BinExp(TOK op, Expression e1, Expression e2) {
		super(op);
		this.e1 = e1;
		this.e2 = e2;
		if (e1 != null && e2 != null) {
			this.start = e1.start;
			this.length = e2.start + e2.length - e1.start;
		}
	}

}
