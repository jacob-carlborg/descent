package descent.internal.compiler.parser;


public abstract class UnaExp extends Expression {
	
	public Expression e1;

	public UnaExp(Loc loc, TOK op, Expression e1) {
		super(loc, op);
		this.e1 = e1;		
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		e1 = e1.semantic(sc, context);
	    return this;
	}
	
	@Override
	public Expression syntaxCopy() {
		UnaExp e;

	    e = (UnaExp) copy();
	    e.type = null;
	    e.e1 = e.e1.syntaxCopy();
	    return e;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring(op.toString());
	    expToCBuffer(buf, hgs, e1, op.precedence, context);
	}

}
